package dev.arildo.iris.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import dev.arildo.iris.plugin.visitor.IrisMockVisitorFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class IrisMockPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.withPlugin("com.android.application") {
            val androidComponents = target.extensions
                .getByType(AndroidComponentsExtension::class)

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
}
