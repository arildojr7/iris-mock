// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}
subprojects {
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("dev.arildo:iris-mock"))
                .using(project(":iris-mock"))
                .because("working with local development version")
        }
    }
}
