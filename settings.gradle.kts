// This should match the folder name of the project, or else IDEA may complain (see https://youtrack.jetbrains.com/issue/IDEA-317606)
rootProject.name = "LanguageReload"

pluginManagement {
    repositories {
        exclusiveContent {
            forRepository { maven("https://maven.fabricmc.net") { name = "Fabric" } }
            filter {
                includeGroup("net.fabricmc")
                includeGroup("fabric-loom")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    // This plugin allows Gradle to automatically download arbitrary versions of Java for you
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

includeBuild("build-logic")

include(":common", ":fabric", ":neoforge")