package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.InterceptedRequest
import dev.arildo.iris.mock.callmodifier.DelayModifier
import dev.arildo.iris.mock.util.readRequestBody

/**
 * @return a generic empty json object
 */
inline val emptyJsonObject: String
    get() = "{}"

/**
 * @return a generic empty json array
 */
inline val emptyJsonArray: String
    get() = "[]"

fun InterceptedRequest.delay(timeMillis: Long) {
    if (shouldIntercept) {
        addEvent(DelayModifier(irisMockScope.chain.hashCode(), timeMillis))
    }
}

/**
 * Verify if original request body contains the [value]
 * @return true if original request body contains the [value]
 */
fun InterceptedRequest.containsInRequestBody(value: String, ignoreCase: Boolean = true): Boolean =
    readRequestBody(irisMockScope.chain.request()).contains(value, ignoreCase)
