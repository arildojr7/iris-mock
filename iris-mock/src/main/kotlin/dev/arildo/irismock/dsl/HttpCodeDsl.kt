@file:JvmMultifileClass
@file:Suppress("unused")

package dev.arildo.irismock.dsl

import dev.arildo.irismock.InterceptedRequest
import dev.arildo.irismock.callmodifier.SetHttpCodeResponseModifier
import dev.arildo.irismock.util.HttpCode

/**
 * Change the response HTTP status code to the specified one
 */
fun InterceptedRequest.setHttpCode(httpCode: HttpCode) {
    addModifier(SetHttpCodeResponseModifier(irisMockScope.chain.hashCode(), httpCode))
}
