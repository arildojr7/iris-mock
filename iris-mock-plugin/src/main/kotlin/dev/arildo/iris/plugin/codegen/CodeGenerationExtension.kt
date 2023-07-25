package dev.arildo.iris.plugin.codegen

import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.analyzer.AnalysisResult.RetryWithAdditionalRoots
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File

/**
 * Adapted from https://github.com/square/anvil
 */
internal class CodeGenerationExtension(
    private val codeGenDir: File,
    private val codeGenerators: List<CodeGenerator>,
    private val moduleDescriptorFactory: IrisMockModuleDescriptorImpl.Factory
) : AnalysisHandlerExtension {

    private var didRecompile = false

    override fun doAnalysis(
        project: Project,
        module: ModuleDescriptor,
        projectContext: ProjectContext,
        files: Collection<KtFile>,
        bindingTrace: BindingTrace,
        componentProvider: ComponentProvider
    ): AnalysisResult? {
        // Tell the compiler that we have something to do in the analysisCompleted() method if
        // necessary.
        return if (!didRecompile) AnalysisResult.EMPTY else null
    }

    override fun analysisCompleted(
        project: Project,
        module: ModuleDescriptor,
        bindingTrace: BindingTrace,
        files: Collection<KtFile>
    ): AnalysisResult? {
        if (didRecompile) return null
        didRecompile = true

        val irisMockModule = moduleDescriptorFactory.create(module)
        irisMockModule.addFiles(files)

        codeGenDir.listFiles()?.forEach {
            check(it.deleteRecursively()) { "Could not clean file: $it" }
        }

        codeGenerators.forEach { it.generateCode(codeGenDir, irisMockModule, files) }

        // This restarts the analysis phase and will include our files.
        return RetryWithAdditionalRoots(
            bindingContext = bindingTrace.bindingContext,
            moduleDescriptor = irisMockModule,
            additionalJavaRoots = emptyList(),
            additionalKotlinRoots = listOf(codeGenDir),
            addToEnvironment = true
        )
    }
}
