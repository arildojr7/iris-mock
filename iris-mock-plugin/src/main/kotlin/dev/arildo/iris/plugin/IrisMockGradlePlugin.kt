package dev.arildo.iris.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import dev.arildo.iris.plugin.visitor.IrisMockVisitorFactory
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class IrisMockGradlePlugin : KotlinCompilerPluginSupportPlugin {
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
        target.extensions.create("irisMock", IrisMockGradleExtension::class.java)
    }

    override fun getCompilerPluginId(): String = "dev.arildo.iris-mock-plugin"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "dev.arildo",
        artifactId = "iris-mock-plugin",
        version = VERSION
    )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(IrisMockGradleExtension::class.java)
        val annotation = extension.redactedAnnotation

        if (annotation.get() == DEFAULT_ANNOTATION) { // todo verify why this check
            project.configurations.getByName("implementation").dependencies.add(
                project.dependencies.create("dev.arildo:iris-mock:$VERSION")
            )
        }

        val enabled = extension.enabled.get()

        return project.provider {
            listOf(
                SubpluginOption(key = "enabled", value = enabled.toString()),
                SubpluginOption(
                    key = "replacementString",
                    value = extension.replacementString.get()
                ),
                SubpluginOption(key = "redactedAnnotation", value = annotation.get())
            )
        }
    }
}
