import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    id("multiloader-loader")
    alias(libs.plugins.moddev)
}

neoForge {
    version = libs.versions.neoforge.asProvider().get()
    // Automatically enable NeoForge AccessTransformers if the file exists
    val accessTransformer = common.file("src/main/resources/META-INF/accesstransformer.cfg")

    if (accessTransformer.exists()) {
        accessTransformers.from(accessTransformer.absolutePath)
    }

    parchment {
        minecraftVersion = libs.versions.parchmentmc.get()
        mappingsVersion = libs.versions.parchment.get()
    }

    validateAccessTransformers.set(true)

    runs {
        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", mod.id)
            ideName = "NeoForge ${name.uppercaseFirstChar()} (${project.path})" // Unify the run config names with fabric

            listOf(
                "-XX:+AllowEnhancedClassRedefinition",
                "-XX:+IgnoreUnrecognizedVMOptions",
                "-Dmixin.debug.export=true"
            ).forEach { jvmArgument(it) }
        }
        register("client") {
            client()
        }
        register("server") {
            server()
        }
    }

    mods {
        register(mod.id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main {
    resources { srcDir("src/generated/resources") }
}

tasks {
    processResources {
        exclude("${mod.id}.accesswidener")
    }
}