// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.2.2" apply false
    id("com.android.library") version "7.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.21" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.3.2"
}
subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("dev.arildo:iris-mock"))
                .using(project(":iris-mock"))
                .because("working with local development version")
        }
    }
}
