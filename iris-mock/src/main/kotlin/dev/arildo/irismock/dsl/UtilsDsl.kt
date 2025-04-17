@file:Suppress("unused")

package dev.arildo.irismock.dsl

import dev.arildo.irismock.InterceptedRequest
import dev.arildo.irismock.callmodifier.DelayModifier
import dev.arildo.irismock.util.readRequestBody
import kotlin.random.Random

/**
 * @return a generic empty json object
 */
inline val emptyJsonObject: String get() = "{}"

/**
 * @return a generic empty json array
 */
inline val emptyJsonArray: String get() = "[]"

fun InterceptedRequest.delay(timeMillis: Long) {
    if (shouldIntercept) {
        addModifier(DelayModifier(irisMockScope.chain.hashCode(), timeMillis))
    }
}

fun InterceptedRequest.randomDelay(minTimeMillis: Long, maxTimeMillis: Long) {
    delay(Random.nextLong(minTimeMillis, maxTimeMillis))
}

/**
 * Verify if original request body contains the [value]
 * @return true if original request body contains the [value]
 */
fun InterceptedRequest.containsInRequestBody(value: String, ignoreCase: Boolean = true): Boolean =
    readRequestBody(irisMockScope.chain.request()).contains(value, ignoreCase)
