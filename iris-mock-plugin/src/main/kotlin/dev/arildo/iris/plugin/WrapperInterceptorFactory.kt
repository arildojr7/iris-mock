package dev.arildo.iris.plugin

import org.jetbrains.kotlin.psi.KtFile

const val PACKAGE = "dev.arildo.iris.mock"

internal fun wrapperInterceptorFactory(classes: List<KtFile>) = """
package $PACKAGE

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okio.BufferedSource
import okio.GzipSource
import okio.Okio
import okio.buffer
import dev.arildo.iris.mock.util.log
import dev.arildo.iris.mock.IrisMock
${classes.joinToString("\n") { "import ${it.packageFqName.asString()}.${it.name}" }}

class IrisWrapperInterceptor : Interceptor {
    private val headerPlaceholder = "IRIS_MOCK"
    private val interceptors = listOf<Interceptor>(
${classes.joinToString(",\n") { "        ${it.name}()" }}
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