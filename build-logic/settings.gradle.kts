pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    // https://github.com/radoslaw-panuszewski/typesafe-conventions-gradle-plugin
    id("dev.panuszewski.typesafe-conventions") version "0.7.3"
}

rootProject.name = "build-logic"