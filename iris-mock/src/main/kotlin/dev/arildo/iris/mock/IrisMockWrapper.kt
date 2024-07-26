@file:Suppress("UNCHECKED_CAST", "UNUSED")

package dev.arildo.iris.mock

import dev.arildo.iris.mock.callmodifier.BehaviourModifier
import dev.arildo.iris.mock.callmodifier.CallModifier
import dev.arildo.iris.mock.callmodifier.CustomResponseBodyModifier
import dev.arildo.iris.mock.callmodifier.DelayModifier
import dev.arildo.iris.mock.callmodifier.RequestModifier
import dev.arildo.iris.mock.callmodifier.ResponseModifier
import dev.arildo.iris.mock.util.HttpCode
import dev.arildo.iris.mock.util.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response

internal class IrisMockWrapper : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // iterate over interceptors to process all DSL functions
        getInterceptors().onEach { it.intercept(chain) }

        val callModifiers = IrisMock.callModifiers.filter { it.chainHashCode == chain.hashCode() }

        callModifiers.filterIsInstance<BehaviourModifier>().forEach { modifier ->
            when (modifier) {
                is DelayModifier -> runBlocking(Dispatchers.IO) { delay(modifier.timeMillis) }
            }
        }

        val customRequest = processRequestModifiers(callModifiers, chain)
        val customResponse = processResponseModifiers(
            callModifiers, chain, customRequest?.build() ?: chain.request()
        )

        // use modified request/response when available, otherwise proceed with the real ones
        val request = customRequest?.build() ?: chain.request()
        val response = customResponse?.request(request)?.build() ?: chain.proceed(request)

        if (IrisMock.enableLogs) response.log()

        return response
    }

    private fun processResponseModifiers(
        callModifiers: List<CallModifier>,
        chain: Interceptor.Chain,
        request: Request,
    ): Response.Builder? {
        // when the response body is modified, the other modifications will be applied in this
        // custom Response.Builder, otherwise it will proceed with the real call and then apply them
        val hasCustomResponseBody = callModifiers.any { it is CustomResponseBodyModifier }
        var customResponse: Response.Builder? = null

        callModifiers.filterIsInstance<ResponseModifier>().forEach { modifier ->
            if (customResponse == null) {
                customResponse = if (hasCustomResponseBody) {
                    createBaseResponseBuilder()
                } else {
                    chain.proceed(request).newBuilder()
                }
            }

            customResponse?.let(modifier::process)
        }
        return customResponse
    }

    private fun processRequestModifiers(
        callModifiers: List<CallModifier>,
        chain: Interceptor.Chain
    ): Request.Builder? {
        var customRequest: Request.Builder? = null

        callModifiers.filterIsInstance<RequestModifier>().forEach { modifier ->
            if (customRequest == null) customRequest = chain.request().newBuilder()

            customRequest?.let(modifier::process)
        }
        return customRequest
    }

    private fun createBaseResponseBuilder(): Response.Builder {
        return Response.Builder().code(HttpCode.OK.code).protocol(Protocol.HTTP_1_1)
    }

    private fun getInterceptors(): List<Interceptor> = runCatching {
        // as the interceptor list is generated in build time, it's retrieved through reflection
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
