package dev.arildo.iris.mock

import dev.arildo.iris.mock.IrisMock.enableLogs
import dev.arildo.iris.mock.IrisMock.headerPlaceholder
import dev.arildo.iris.mock.util.log
import okhttp3.Interceptor
import okhttp3.Response

class IrisMockWrapper : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        IrisMock.interceptors.forEach {
            val response = runCatching { it.intercept(chain) }.getOrElse { return@forEach }
            if (response.header(headerPlaceholder).isNullOrBlank()) {
                chain.proceed(chain.request()).close() // TODO investigate how to avoid this call
                return response.takeIf { enableLogs }?.let { it.log() } ?: response
            }
        }
        val response = chain.proceed(chain.request())
        return response.takeIf { enableLogs }?.let { it.log() } ?: response
    }

}
