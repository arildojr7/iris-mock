package dev.arildo.iris.mock

import dev.arildo.iris.mock.callmodifier.CallModifier
import dev.arildo.iris.mock.dsl.IrisMockDslMarker
import dev.arildo.iris.mock.util.Method

@IrisMockDslMarker
open class InterceptedRequest internal constructor(
    internal val irisMockScope: IrisMockScope,
    internal val url: String,
    internal val method: Method,
    internal val shouldIntercept: Boolean,
) {
    internal fun addModifier(modifier: CallModifier) = ModifierProcessor.callModifiers.add(modifier)
}

internal fun IrisMockScope.interceptCall(
    contains: String,
    endsWith: String,
    method: Method
): InterceptedRequest {
    val request = chain.request()
    val url = request.url().toString()
    val requestMethod = request.method()

    return InterceptedRequest(
        irisMockScope = this,
        url = url,
        method = method,
        shouldIntercept = when {
            endsWith.isNotBlank() && contains.isNotBlank() -> {
                throw IllegalArgumentException("Must provide only one string matcher")
            }

            endsWith.isNotBlank() -> {
                url.endsWith(endsWith, true) && requestMethod == method.name
            }

            contains.isNotBlank() -> {
                url.contains(contains, true) && requestMethod == method.name
            }

            else -> throw IllegalArgumentException("Must provide a string to match on url")
        }
    ).also {
        if (it.shouldIntercept) IrisMock.logger.info("Intercepting: [$method] $url")
    }
}
