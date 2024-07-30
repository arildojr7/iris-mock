@file:Suppress("UNCHECKED_CAST", "UNUSED")

package dev.arildo.iris.mock

import dev.arildo.iris.mock.util.INTERCEPTORS_FIELD
import dev.arildo.iris.mock.util.IRIS_MOCK_INTERCEPTORS
import dev.arildo.iris.mock.util.log
import okhttp3.Interceptor
import okhttp3.Response

/*
 * When using the iris-mock plugin to auto-inject the interceptors
 * this class is called to instantiate the annotated interceptor
 * implementations, granting that all of them are considered.
 *
 * When using only the iris-mock api dependency, this class is not called,
 * since the interceptor is being added manually to the OkHttp builder.
 */
internal class IrisMockWrapper : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // iterate over annotated interceptors to process all DSL functions
        getInterceptors().onEach { it.intercept(chain) }

        val response = ModifierProcessor.process(chain)

        if (IrisMock.enableLogs) response.log()

        return response
    }

    private fun getInterceptors(): List<Interceptor> = runCatching {
        // as the interceptor list is generated in build time, it's retrieved through reflection
        Class.forName(IRIS_MOCK_INTERCEPTORS)
            .getDeclaredField(INTERCEPTORS_FIELD)
            .apply { isAccessible = true }
            .get(listOf<Interceptor>()) as List<Interceptor>
    }.getOrDefault(emptyList())
}
