package dev.arildo.irismock

import dev.arildo.irismock.callmodifier.BehaviourModifier
import dev.arildo.irismock.callmodifier.CallModifier
import dev.arildo.irismock.callmodifier.CustomResponseBodyModifier
import dev.arildo.irismock.callmodifier.DelayModifier
import dev.arildo.irismock.callmodifier.RequestModifier
import dev.arildo.irismock.callmodifier.ResponseModifier
import dev.arildo.irismock.util.HttpCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response

internal object ModifierProcessor {
    val callModifiers = mutableSetOf<CallModifier>()

    fun process(chain: Interceptor.Chain): Response {
        val modifiersForCurrentChain = getCurrentChainModifiers(chain)

        modifiersForCurrentChain.behaviourModifiers.forEach { modifier ->
            when (modifier) {
                is DelayModifier -> runBlocking(Dispatchers.IO) { delay(modifier.timeMillis) }
            }
        }

        val customRequest = processRequestModifiers(
            requestModifiers = modifiersForCurrentChain.requestModifiers,
            chain = chain
        )
        val customResponse = processResponseModifiers(
            responseModifier = modifiersForCurrentChain.responseModifiers,
            chain = chain,
            request = customRequest?.build() ?: chain.request()
        )

        // use modified request/response when available, otherwise proceed with the real ones
        val request = customRequest?.build() ?: chain.request()
        val response = customResponse?.request(request)?.build() ?: chain.proceed(request)

        return response
    }

    private fun processResponseModifiers(
        responseModifier: List<ResponseModifier>,
        chain: Interceptor.Chain,
        request: Request,
    ): Response.Builder? {
        // when the response body is modified, the other modifications will be applied in the
        // custom Response.Builder, otherwise it will proceed with the real call and then apply them
        val hasCustomResponseBody = responseModifier.any { it is CustomResponseBodyModifier }
        var customResponse: Response.Builder? = null

        responseModifier.forEach { modifier ->
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
        requestModifiers: List<RequestModifier>,
        chain: Interceptor.Chain
    ): Request.Builder? {
        var customRequest: Request.Builder? = null

        requestModifiers.forEach { modifier ->
            if (customRequest == null) customRequest = chain.request().newBuilder()

            customRequest?.let(modifier::process)
        }
        return customRequest
    }

    private val List<CallModifier>.requestModifiers get() = filterIsInstance<RequestModifier>()

    private val List<CallModifier>.responseModifiers get() = filterIsInstance<ResponseModifier>()

    private val List<CallModifier>.behaviourModifiers get() = filterIsInstance<BehaviourModifier>()

    private fun getCurrentChainModifiers(chain: Interceptor.Chain) = callModifiers
        .filter { it.chainHashCode == chain.hashCode() }

    private fun createBaseResponseBuilder(): Response.Builder {
        return Response.Builder().code(HttpCode.OK.code).protocol(Protocol.HTTP_1_1)
    }
}
