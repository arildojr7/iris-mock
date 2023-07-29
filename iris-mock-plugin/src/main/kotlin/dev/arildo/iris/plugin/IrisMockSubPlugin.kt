package dev.arildo.iris.plugin

import dev.arildo.iris.plugin.BuildConfig.PLUGIN_ARTIFACT_ID
import dev.arildo.iris.plugin.BuildConfig.PLUGIN_GROUP_ID
import dev.arildo.iris.plugin.BuildConfig.PLUGIN_ID
import dev.arildo.iris.plugin.BuildConfig.PLUGIN_VERSION
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import java.io.File

class IrisMockSubPlugin : KotlinCompilerPluginSupportPlugin {
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>) = true

    override fun getCompilerPluginId() = PLUGIN_ID

    override fun getPluginArtifact() = SubpluginArtifact(
        groupId = PLUGIN_GROUP_ID,
        artifactId = PLUGIN_ARTIFACT_ID,
        version = PLUGIN_VERSION
    )

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        return kotlinCompilation.target.project.let { project ->
            project.provider {
                listOf(
                    SubpluginOption(
                        key = "src-gen-dir",
                        value = "${project.buildDir}${File.separator}iris-mock"
                    )
                )
            }
        }
    }
}
