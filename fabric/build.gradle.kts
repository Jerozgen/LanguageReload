import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    id("multiloader-loader")
    alias(libs.plugins.loom)
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${libs.versions.parchmentmc.get()}:${libs.versions.parchment.get()}@zip")
    })

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)

    modImplementation(libs.modmenu)
}

loom {
    val accessWidener = common.file("src/main/resources/${mod.id}.accesswidener")

    if (accessWidener.exists()) {
        accessWidenerPath.set(accessWidener)
    }

    mixin {
        defaultRefmapName.set("${mod.id}.refmap.json")
    }

    runs {
        configureEach {
            configName = "Fabric ${name.uppercaseFirstChar()}"
            runDir("runs/${name}")
            ideConfigGenerated(true)

            vmArgs(
                "-XX:+AllowEnhancedClassRedefinition",
                "-XX:+IgnoreUnrecognizedVMOptions",
                "-Dmixin.debug.export=true"
            )
        }
    }
}