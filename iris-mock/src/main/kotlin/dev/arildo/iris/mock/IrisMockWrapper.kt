@file:Suppress("UNCHECKED_CAST", "unused")

package dev.arildo.iris.mock

import dev.arildo.iris.mock.util.IRIS_HEADER_KEY
import dev.arildo.iris.mock.util.log
import okhttp3.Interceptor
import okhttp3.Response

internal class IrisMockWrapper : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        getInterceptors().forEach {
            val response = runCatching { it.intercept(chain) }.getOrElse { return@forEach }
            if (response.header(IRIS_HEADER_KEY).isNullOrBlank()) {
                return response.takeIf { IrisMock.enableLogs }?.log() ?: response
            }
        }

        val response = chain.proceed(chain.request())
        return response.takeIf { IrisMock.enableLogs }?.log() ?: response
    }

    private fun getInterceptors(): List<Interceptor> = runCatching {
        // as the interceptor list is generated in build time, it's retrieved though reflection
        Class.forName(IRIS_MOCK_INTERCEPTORS)
            .getDeclaredField(INTERCEPTORS_FIELD)
            .apply { isAccessible = true }
            .get(listOf<Interceptor>()) as List<Interceptor>
    }.getOrDefault(emptyList())

    companion object {
        const val IRIS_MOCK_INTERCEPTORS = "dev.arildo.iris.mock.IrisMockContainer"
        const val INTERCEPTORS_FIELD = "interceptors"
    }
}
