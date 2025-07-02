import org.gradle.api.Project
import org.gradle.language.jvm.tasks.ProcessResources

/**
 * Retrieves the mod data from the project.
 *
 * @return the mod data
 */
val Project.mod: ModData get() = ModData(this)

/**
 * Mod links.
 *
 * @property modrinth the link to the mod on Modrinth
 * @property curseforge the link to the mod on Curseforge
 */
data class ModLinks(
    val modrinth: String,
    val curseforge: String,
)

/**
 * Retrieves a property value from the project based on the provided key.
 *
 * @param key the property key to look up in the project's properties.
 * @return the value of the property as a string, or null if the property does not exist.
 */
fun Project.prop(key: String): String? = findProperty(key)?.toString()

/**
 * Retrieves common project.
 *
 * @return the common project
 */
val Project.common get() = rootProject.project(":common")

@JvmInline
value class ModData(private val project: Project) {
    val id: String get() = modProp("id")
    val name: String get() = modProp("name")
    val version: String get() = modProp("version")
    val group: String get() = modProp("group")
    val archivesBaseName: String get() = modProp("archives_base_name")
    val author: String get() = modProp("author")
    val description: String get() = modProp("description")
    val license: String get() = modProp("license")
    val credits: String get() = modProp("credits")
    val homepage: String get() = modProp("homepage")
    val sources: String get() = modProp("sources")
    val issues: String get() = modProp("issues")
    val links: ModLinks
        get() = ModLinks(
            modrinth = linkProp("modrinth"),
            curseforge = linkProp("curseforge"),
        )

    /**
     * Retrieves a mod property value from the project based on the provided key.
     *
     * @param key the mod property key to look up in the project's properties.
     * @return the value of the mod property as a string.
     */
    private fun modPropOrNull(key: String) = project.prop("mod.$key")
    private fun modProp(key: String) = requireNotNull(modPropOrNull(key)) { "Missing 'mod.$key'" }

    /**
     * Retrieves a link property value from the project based on the provided key.
     *
     * @param key the link property key to look up in the project's properties.
     * @return the value of the link property as a string.
     */
    private fun linkPropOrNull(key: String) = modPropOrNull("links.$key")
    private fun linkProp(key: String) = requireNotNull(linkPropOrNull(key)) { "Missing 'links.$key'" }

    // private fun depOrNull(key: String) = project.prop("deps.$key")
    // private fun dep(key: String) = requireNotNull(depOrNull(key)) { "Missing 'deps.$key'" }
}

/**
 * Returns a properties map transformed based on the file type.
 * Line breaks are escaped to '\\n' if the file is JSON.
 *
 * @param filePath The path of the file to be processed (e.g. "file.json").
 * @param props The original properties map with literal line breaks.
 * @return The properties map appropriate for the file type.
 */
fun getTransformedProps(filePath: String, props: Map<String, Any>): Map<String, Any> {
    val jsonExtensions = listOf("json", "mcmeta")

    return when {
        jsonExtensions.any { filePath.endsWith(it) } -> {
            props.mapValues { (_, value) ->
                if (value is String) value.replace("\n", "\\\\n") else value
            }
        }
        else -> props
    }
}

/**
 * Applies a set of properties to a `ProcessResources` task, based on the project's configuration
 * and additional resource files.
 *
 * @param properties a map of properties to be applied to the resource files.
 * @param files an iterable collection of file paths to which the properties will be applied.
 */
fun ProcessResources.applyProperties(properties: Map<String, Any>, files: Iterable<String>) {
    filesMatching(files) {
        val props = getTransformedProps(relativePath.pathString, properties)
        expand(props)
    }

    inputs.properties(properties)
}