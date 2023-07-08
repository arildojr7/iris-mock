package dev.arildo.iris.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import dev.arildo.iris.processor.factory.blankInterceptorFactory
import dev.arildo.iris.processor.factory.wrapperInterceptorFactory
import java.io.OutputStream

class IrisMockProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {
    private val annotationClass = "dev.arildo.iris.mock.annotation.IrisMockInterceptor"

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver
            .getSymbolsWithAnnotation(annotationClass)
            .filterIsInstance<KSClassDeclaration>()

        // Exit from the processor in case nothing is annotated with @IrisMockInterceptor
        if (!symbols.iterator().hasNext()) {
            runCatching {
                createClassFile(resolver).use { it += blankInterceptorFactory() }
            }
            return emptyList()
        }

        // check if only Interceptor classes have been annotated with @IrisMockInterceptor
        assertClassesAreInterceptorSubType(symbols)

        createClassFile(resolver).use { classFile ->
            classFile += wrapperInterceptorFactory(symbols)
        }

        return symbols.filterNot { it.validate() }.toList()
    }

    private fun createClassFile(resolver: Resolver) = codeGenerator.createNewFile(
        dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
        packageName = "dev.arildo.iris.mock",
        fileName = "IrisWrapperInterceptor"
    )

    private fun assertClassesAreInterceptorSubType(symbols: Sequence<KSClassDeclaration>) {
        symbols.forEach { kClass ->
            if (!kClass.superTypes.all { it.resolve().toString() == "Interceptor" }) {
                throw ClassCastException("Only classes implementing okhttp3.Interceptor can be annotated: ${kClass.simpleName.asString()}")
            }
        }
    }

    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }
}
