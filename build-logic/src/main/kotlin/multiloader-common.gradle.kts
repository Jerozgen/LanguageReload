plugins {
    idea
    `java-library`
    `maven-publish`
}

version = "${mod.version}+${libs.versions.minecraft.asProvider().get()}-${project.name}"
group = mod.group

base {
    archivesName.set(mod.archivesBaseName)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get().toInt()))
    withSourcesJar()
    withJavadocJar()
}

repositories {
    // https://docs.gradle.org/current/userguide/declaring_repositories.html#declaring_content_exclusively_found_in_one_repository
    exclusiveContent {
        forRepositories(
            maven("https://maven.parchmentmc.org") { name = "ParchmentMC" },
            maven("https://maven.neoforged.net/releases") { name = "NeoForge" }
        )
        filter { includeGroup("org.parchmentmc.data") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.fabricmc.net") { name = "Fabric" } }
        filter { includeGroup("net.fabricmc") }
    }
    exclusiveContent {
        forRepository { maven("https://repo.spongepowered.org/repository/maven-public") { name = "Sponge" } }
        filter { includeGroupAndSubgroups("org.spongepowered") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.terraformersmc.com") { name = "TerraformersMC" } }
        filter { includeGroup("com.terraformersmc") }
    }
    maven("https://maven.blamejared.com") { name = "BlameJared" }

    mavenCentral()
}

// Declare capabilities on the outgoing configurations.
// Read more about capabilities here: https://docs.gradle.org/current/userguide/component_capabilities.html#sec:declaring-additional-capabilities-for-a-local-component
listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { variant ->
    configurations[variant].outgoing {
        capability("${mod.group}:${project.name}:${mod.version}")
        capability("${mod.group}:${base.archivesName.get()}:${mod.version}")
        capability("${mod.group}:${mod.id}-${project.name}-${libs.versions.minecraft.asProvider().get()}:${mod.version}")
        capability("${mod.group}:${mod.id}:${mod.version}")
    }

    publishing.publications.containerWithType(MavenPublication::class.java).configureEach {
        suppressPomMetadataWarningsFor(variant)
    }
}

tasks {
    val modId = mod.id
    val modLogo = "assets/${mod.id}/icon.png"

    named<Jar>("sourcesJar") {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${modId}" }
        }
    }

    named<Jar>("jar") {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${modId}" }
        }

        manifest.attributes(
            mapOf(
                "Specification-Title" to mod.name,
                "Specification-Vendor" to mod.author,
                "Specification-Version"  to version,
                "Implementation-Title" to project.name,
                "Implementation-Vendor" to mod.author,
                "Implementation-Version" to version,
                "Built-On-Minecraft" to libs.versions.minecraft.asProvider().get()
            )
        )
    }

    named<ProcessResources>("processResources") {
        val expandProps = mapOf(
            "modGroup" to mod.group,
            "modId" to mod.id,
            "modName" to mod.name,
            "modVersion" to mod.version,
            "modAuthor" to mod.author,
            "modDescription" to mod.description,
            "modLicense" to mod.license,
            "modCredits" to mod.credits,
            "modHomepage" to mod.homepage,
            "modSources" to mod.sources,
            "modIssues" to mod.issues,
            "modLogo" to modLogo,
            "modLinksModrinth" to mod.links.modrinth,
            "modLinksCurseforge" to mod.links.curseforge,
            "javaVersion" to libs.versions.java.get(),
            "minecraftVersion" to libs.versions.minecraft.asProvider().get(),
            "minecraftVersionRange" to libs.versions.minecraft.range.get(),
            "fabricVersion" to libs.versions.fabric.api.get(),
            "fabricLoaderVersion" to libs.versions.fabric.loader.get(),
            "neoforgeVersion" to libs.versions.neoforge.asProvider().get(),
            "neoforgeVersionRange" to libs.versions.neoforge.range.get(),
            "neoforgeLoaderVersionRange" to libs.versions.neoforge.loader.range.get(),
        )

        val files = listOf("pack.mcmeta", "fabric.mod.json", "*.mixins.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml")
        applyProperties(expandProps, files)
    }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }
    repositories {
        val localMavenUrl = System.getenv("local_maven_url")
        if (!localMavenUrl.isNullOrBlank()) {
            maven { url = uri(localMavenUrl) }
        }
    }
}