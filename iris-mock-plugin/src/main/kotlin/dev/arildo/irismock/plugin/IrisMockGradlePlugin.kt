@file:Suppress("unused")

package dev.arildo.irismock.plugin

import dev.arildo.irismock.plugin.BuildConfig.PLUGIN_VERSION
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.internal.VersionNumber

class IrisMockGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val extension = extensions.create("irisMock", IrisMockExtension::class.java).also {
            if (!it.enabled.get()) return@with
        }

        addIrisMockRuntimeDependency(extension.configuration.get())

        val agpVersion = getAgpVersion()

        when {
            agpVersion < VersionNumber.parse("4.2.0") -> error("android gradle plugin not supported")
            agpVersion < VersionNumber.parse("7.1.0") -> handleAgp42(this)
            agpVersion < VersionNumber.parse("7.2.0") -> handleAgp71(this)
            else -> handleAgp72(this)
        }
    }

    private fun Project.addIrisMockRuntimeDependency(configuration: String) {
        configurations.getByName(configuration).dependencies.add(
            dependencies.create("dev.arildo:iris-mock:$PLUGIN_VERSION")
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
