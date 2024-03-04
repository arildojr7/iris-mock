@file:Suppress("unused")

package dev.arildo.iris.plugin

import dev.arildo.iris.plugin.BuildConfig.PLUGIN_VERSION
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.internal.VersionNumber

class IrisMockGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        addIrisMockRuntimeDependency(project)
        project.plugins.apply(IrisMockSubPlugin::class.java)

        val agpVersion = project.getAgpVersion()
        println("@@@ $agpVersion")

        when {
            agpVersion < VersionNumber.parse("4.2.0") -> error("android gradle plugin not supported")
            agpVersion < VersionNumber.parse("7.1.0") -> handleAgp42(project)
            agpVersion < VersionNumber.parse("7.2.0") -> handleAgp71(project)
            else -> handleAgp72(project)
        }
    }

    private fun addIrisMockRuntimeDependency(project: Project) {
        project.configurations.getByName("implementation").dependencies.add(
            project.dependencies.create("dev.arildo:iris-mock:$PLUGIN_VERSION")
        )
    }

    private fun Project.getAgpVersion(): VersionNumber {
        val buildscript = rootProject.buildscript
        val artifacts =
            buildscript.configurations.getByName("classpath").resolvedConfiguration.resolvedArtifacts

        // Find the Android Gradle plugin dependency
        val androidGradlePlugin = artifacts.singleOrNull {
            it.moduleVersion.id.toString().startsWith("com.android.tools.build:gradle:")
        } ?: error("Could not determine Android Gradle plugin version")

        // Extract the resolved Android Gradle plugin version
        return VersionNumber.parse(androidGradlePlugin.moduleVersion.id.version)
    }
}
