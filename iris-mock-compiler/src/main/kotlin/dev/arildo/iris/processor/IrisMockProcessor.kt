package dev.arildo.iris.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
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
                createClassFile(resolver).use { it += blankInterceptorTemplate }
            }
            return emptyList()
        }

        createClassFile(resolver).use {
            it += "package dev.arildo.iris.mock\n\n"
            symbols.forEach { symbol -> symbol.accept(ImportsVisitor(it), Unit) }

            it += firstPartClass()
            symbols.forEach { symbol -> symbol.accept(InitVisitor(it), Unit) }

            it += secondPartClass()
        }

        return symbols.filterNot { it.validate() }.toList()
    }

    private fun createClassFile(resolver: Resolver) = codeGenerator.createNewFile(
        dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
        packageName = "dev.arildo.iris.mock",
        fileName = "IrisWrapperInterceptor"
    )

    inner class ImportsVisitor(private val file: OutputStream) : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            if (classDeclaration.classKind != ClassKind.CLASS) {
                logger.error(
                    "Only classes can be annotated with @IrisMockInterceptor",
                    classDeclaration
                )
                return
            }

            file += "import ${classDeclaration.qualifiedName?.asString()}\n"
        }
    }

    inner class InitVisitor(private val file: OutputStream) : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            file += "        ${classDeclaration.simpleName.asString()}(),\n"
        }
    }

    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    private fun firstPartClass() = """
import okhttp3.Interceptor
import okhttp3.Response

class IrisWrapperInterceptor : Interceptor {

    private val headerPlaceholder = "IRIS_MOCK"
    private val interceptors = listOf<Interceptor>(
"""

    private fun secondPartClass() = """
        )
    override fun intercept(chain: Interceptor.Chain): Response {
        interceptors.forEach {
            val response = runCatching { it.intercept(chain) }.getOrElse { return@forEach }
            if (!response.header(headerPlaceholder).isNullOrBlank()) {
                chain.proceed(chain.request()).close() // TODO investigate how to avoid this call
                return response.newBuilder().removeHeader(headerPlaceholder).build()
            }
        }
        return chain.proceed(chain.request())
    }
}
"""
}
