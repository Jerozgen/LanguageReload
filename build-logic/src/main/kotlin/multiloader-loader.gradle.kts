plugins {
    idea
    `java-library`
    id("multiloader-common")
}

val commonJava: Configuration by configurations.creating {
    isCanBeResolved = true
}
val commonResources: Configuration by configurations.creating {
    isCanBeResolved = true
}

dependencies {
    compileOnly(project(path = common.path)) {
        capabilities {
            requireCapability("${mod.group}:${mod.id}")
        }
    }
    commonJava(project(path = common.path, configuration = commonJava.name))
    commonResources(project(path = common.path, configuration = commonResources.name))
}

tasks {
    named<JavaCompile>("compileJava") {
        dependsOn(commonJava)
        source(commonJava)
    }

    processResources {
        dependsOn(commonResources)
        from(commonResources)
    }

    named<Javadoc>("javadoc").configure {
        dependsOn(commonJava)
        source(commonJava)
    }

    named<Jar>("sourcesJar") {
        dependsOn(commonJava)
        from(commonJava)
        dependsOn(commonResources)
        from(commonResources)
    }
}
