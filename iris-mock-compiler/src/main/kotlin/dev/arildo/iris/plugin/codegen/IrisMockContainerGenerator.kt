package dev.arildo.iris.plugin.codegen

import com.google.auto.service.AutoService
import dev.arildo.iris.plugin.utils.IRIS_MOCK_ANNOTATION
import dev.arildo.iris.plugin.utils.IRIS_MOCK_CONTAINER
import dev.arildo.iris.plugin.utils.IRIS_MOCK_PACKAGE
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.getSuperNames
import java.io.File

@AutoService(CodeGenerator::class)
internal class IrisMockContainerGenerator : CodeGenerator {

    override fun generateCode(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>,
    ) {
        val annotatedClasses = projectFiles
            .flatMap { it.classesAndInnerClasses() }
            .filter { it.isAnnotatedWithIrisMock() }
            .onEach { it.assertThatImplementsInterceptorInterface() }
            .mapNotNull { it.fqName?.asString() }

        generateIrisMockContainer(
            codeGenDir = codeGenDir,
            packageName = IRIS_MOCK_PACKAGE,
            fileName = IRIS_MOCK_CONTAINER,
            content = irisMockContainer(annotatedClasses)
        )
    }

    private fun KtClassOrObject.isAnnotatedWithIrisMock() =
        annotationEntries.any { it.shortName.toString().contains(IRIS_MOCK_ANNOTATION) }

    private fun KtClassOrObject.assertThatImplementsInterceptorInterface() {
        if (!getSuperNames().contains("Interceptor")) {
            error("${fqName?.shortNameOrSpecial()} does not implements the okhttp3.Interceptor interface")
        }
    }

    private fun KtFile.classesAndInnerClasses(): List<KtClassOrObject> {
        val children = findChildrenByClass(KtClassOrObject::class.java)

        return generateSequence(children.toList()) { list ->
            list.flatMap { it.declarations.filterIsInstance<KtClassOrObject>() }
                .ifEmpty { null }
        }.flatten().toList()
    }
}
