pluginManagement {
    plugins {
        kotlin("jvm") version "1.7.10"
    }
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "iris-mock"
include(":sample")
include(":iris-mock-plugin")