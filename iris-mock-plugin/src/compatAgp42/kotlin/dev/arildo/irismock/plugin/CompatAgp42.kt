package dev.arildo.irismock.plugin

import com.android.build.api.extension.AndroidComponentsExtension
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import dev.arildo.irismock.plugin.util.PLUGIN_APPLICATION
import dev.arildo.irismock.plugin.visitor.IrisMockVisitorFactory
import org.gradle.api.Project

fun handleAgp42(project: Project) {
    project.plugins.withId(PLUGIN_APPLICATION) {
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

        androidComponents.onVariants { variant ->
            variant.setAsmFramesComputationMode(
                FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
            )
            variant.transformClassesWith(
                IrisMockVisitorFactory::class.java,
                InstrumentationScope.ALL
            ) { params ->
                params.transformEpoch.set(System.currentTimeMillis())
            }
        }
    }
}
