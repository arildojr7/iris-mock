pluginManagement {
    plugins {
        id("com.google.devtools.ksp") version "1.8.21-1.0.11"
        kotlin("jvm") version "1.8.21"
    }
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
        maven {
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}

rootProject.name = "iris-mock"
include(":sample")
include(":iris-mock")
include(":iris-mock-plugin")
//include(":iris-mock-ksp")
//include(":kotlin-plugin")
