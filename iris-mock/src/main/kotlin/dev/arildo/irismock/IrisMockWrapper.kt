@file:Suppress("UNUSED")

package dev.arildo.irismock

import dev.arildo.irismock.util.log
import okhttp3.Interceptor
import okhttp3.Response

/*
 * When using the IrisMock plugin to inject the interceptors,
 * this class is called to iterate over them, granting all logic is processed.
 *
 * When using only the IrisMock runtime dependency, this class is not called at all,
 * since the interceptor is being added manually to the OkHttp builder.
 */
internal class IrisMockWrapper : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        IrisMock.interceptors.forEach { it().intercept(chain) }

        val response = ModifierProcessor.process(chain)

        if (IrisMock.enableLogs) response.log()

        return response
    }
}
