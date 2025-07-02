plugins {
    id("multiloader-common")
    alias(libs.plugins.moddev)
}

neoForge {
    neoFormVersion = libs.versions.neoform.get()
    // Automatically enable AccessTransformers if the file exists
    val accessTransformer = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (accessTransformer.exists()) {
        accessTransformers.from(accessTransformer.absolutePath)
    }
    parchment {
        minecraftVersion = libs.versions.parchmentmc.get()
        mappingsVersion = libs.versions.parchment.get()
    }

    validateAccessTransformers.set(true)
}

dependencies {
    compileOnly(libs.mixin)
    // Fabric and NeoForge both bundle MixinExtras, so it is safe to use it in common
    libs.mixinextras.common.get().let {
        compileOnly(it)
        annotationProcessor(it)
    }
}

val commonJava: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

val commonResources: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

artifacts {
    afterEvaluate {
        val mainSourceSet = sourceSets.main.get()
        mainSourceSet.java.sourceDirectories.files.forEach {
            add(commonJava.name, it)
        }
        mainSourceSet.resources.sourceDirectories.files.forEach {
            add(commonResources.name, it)
        }
    }
}
