@file:Suppress("unused")

package dev.arildo.iris.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import dev.arildo.iris.plugin.BuildConfig.PLUGIN_VERSION
import dev.arildo.iris.plugin.visitor.IrisMockVisitorFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class IrisMockGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create("irisMock", IrisMockGradleExtension::class.java)
        addIrisMockRuntimeDependency(target)

        target.plugins.apply(IrisMockSubPlugin::class.java)

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

    private fun addIrisMockRuntimeDependency(project: Project) {
        project.configurations.getByName("implementation").dependencies.add(
            project.dependencies.create("dev.arildo:iris-mock:$PLUGIN_VERSION")
        )
    }
}
