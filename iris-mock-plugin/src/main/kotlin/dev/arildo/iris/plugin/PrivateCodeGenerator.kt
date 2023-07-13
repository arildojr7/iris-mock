package dev.arildo.iris.plugin

import dev.arildo.iris.plugin.util.CodeGenerator
import dev.arildo.iris.plugin.util.GeneratedFile
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

internal abstract class PrivateCodeGenerator : CodeGenerator {
    final override fun generateCode(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>
    ): Collection<GeneratedFile> {
        generateCodePrivate(codeGenDir, module, projectFiles)
        return emptyList()
    }

    protected abstract fun generateCodePrivate(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>
    )
}
