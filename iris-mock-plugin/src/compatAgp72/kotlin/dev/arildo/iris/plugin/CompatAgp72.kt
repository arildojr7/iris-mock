package dev.arildo.iris.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import dev.arildo.iris.plugin.util.PLUGIN_APPLICATION
import dev.arildo.iris.plugin.visitor.IrisMockVisitorFactory7Dot2
import org.gradle.api.Project

fun handleAgp72(project: Project) {
    project.plugins.withId(PLUGIN_APPLICATION) {
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

        androidComponents.onVariants { variant ->
            variant.instrumentation.setAsmFramesComputationMode(
                FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
            )
            variant.instrumentation.transformClassesWith(
                IrisMockVisitorFactory7Dot2::class.java,
                InstrumentationScope.ALL
            ) { params ->
                params.transformEpoch.set(System.currentTimeMillis())
            }
        }
    }
}
