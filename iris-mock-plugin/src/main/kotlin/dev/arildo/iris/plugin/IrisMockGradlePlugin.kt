@file:Suppress("unused")

package dev.arildo.iris.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import dev.arildo.iris.plugin.BuildConfig.PLUGIN_ARTIFACT_ID
import dev.arildo.iris.plugin.BuildConfig.PLUGIN_GROUP_ID
import dev.arildo.iris.plugin.BuildConfig.PLUGIN_ID
import dev.arildo.iris.plugin.BuildConfig.PLUGIN_VERSION
import dev.arildo.iris.plugin.util.srcGenDirName
import dev.arildo.iris.plugin.visitor.IrisMockVisitorFactory
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import java.io.File

class IrisMockGradlePlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project) {
        target.extensions.create("irisMock", IrisMockGradleExtension::class.java)

        target.pluginManager.withPlugin("com.android.application") {
            val androidComponents = target.extensions.getByType(AndroidComponentsExtension::class)

            androidComponents.onVariants { variant ->
                variant.instrumentation.setAsmFramesComputationMode(
                    FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
                )
                variant.instrumentation.transformClassesWith(
                    IrisMockVisitorFactory::class.java,
                    InstrumentationScope.ALL
                ) { params ->
                    params.transformEpoch.set(System.currentTimeMillis())
                }
            }
        }
    }

    override fun getCompilerPluginId(): String = PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = PLUGIN_GROUP_ID,
        artifactId = PLUGIN_ARTIFACT_ID,
        version = PLUGIN_VERSION
    )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project

        project.configurations.getByName("implementation").dependencies.add(
            project.dependencies.create("dev.arildo:iris-mock:$PLUGIN_VERSION")
        )

        return project.provider {
            listOf(
                SubpluginOption(
                    key = srcGenDirName,
                    value = "${project.buildDir}${File.separator}iris-mock"
                )
            )
        }
    }
}
