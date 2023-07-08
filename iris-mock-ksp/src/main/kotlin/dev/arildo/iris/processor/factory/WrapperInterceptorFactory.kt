package dev.arildo.iris.processor.factory

import com.google.devtools.ksp.symbol.KSClassDeclaration

internal fun wrapperInterceptorFactory(classes: Sequence<KSClassDeclaration>) = """
package dev.arildo.iris.mock

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okio.BufferedSource
import okio.GzipSource
import okio.Okio
import okio.buffer
import dev.arildo.iris.mock.util.log
import dev.arildo.iris.mock.IrisMock
${classes.joinToString("\n") { "import ${it.qualifiedName?.asString()}" }}

class IrisWrapperInterceptor : Interceptor {
    private val headerPlaceholder = "IRIS_MOCK"
    private val interceptors = listOf<Interceptor>(
${classes.joinToString(",\n") { "        ${it.simpleName.asString()}()" }}
    )
    override fun intercept(chain: Interceptor.Chain): Response {
        interceptors.forEach {
            val response = runCatching { it.intercept(chain) }.getOrElse { return@forEach }
            if (response.header(headerPlaceholder).isNullOrBlank()) {
                chain.proceed(chain.request()).close() // TODO investigate how to avoid this call
                return response.takeIf { IrisMock.enableLogs }?.let { it.log() } ?: response
            }
        }
        val response = chain.proceed(chain.request())
        return response.takeIf { IrisMock.enableLogs }?.let { it.log() } ?: response
    }
}
 """
