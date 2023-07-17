package dev.arildo.iris.plugin

import com.google.auto.service.AutoService
import dev.arildo.iris.plugin.util.ClassReference
import dev.arildo.iris.plugin.util.CodeGenerator
import dev.arildo.iris.plugin.util.IRIS_MOCK_INTERCEPTOR
import dev.arildo.iris.plugin.util.IRIS_WRAPPER_NAME
import dev.arildo.iris.plugin.util.IRIS_WRAPPER_PACKAGE
import dev.arildo.iris.plugin.util.classAndInnerClassReferences
import dev.arildo.iris.plugin.util.createGeneratedFile
import dev.arildo.iris.plugin.util.fq
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

@AutoService(CodeGenerator::class)
internal class WrapperCodeGenerator : PrivateCodeGenerator() {

    override fun generateCodePrivate(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>
    ) {
        val annotatedClasses = projectFiles
            .classAndInnerClassReferences(module)
            .filter { it.isAnnotatedWithIrisMock() }

        createGeneratedFile(
            codeGenDir = codeGenDir,
            packageName = IRIS_WRAPPER_PACKAGE,
            fileName = IRIS_WRAPPER_NAME,
            content = wrapperInterceptorFactory(annotatedClasses)
        )
    }

}

private fun ClassReference.Psi.isAnnotatedWithIrisMock(): Boolean {
    return annotations.any {
        it.annotation.fq(module)?.fqName?.asString()?.contains(IRIS_MOCK_INTERCEPTOR)
            ?: false
    }
}
