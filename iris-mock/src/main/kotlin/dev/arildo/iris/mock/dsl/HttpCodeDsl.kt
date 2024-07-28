@file:JvmMultifileClass
@file:Suppress("unused")

package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.InterceptedRequest
import dev.arildo.iris.mock.callmodifier.SetHttpCodeResponseModifier
import dev.arildo.iris.mock.util.HttpCode

/**
 * Change the response HTTP status code to the specified one
 */
fun InterceptedRequest.setHttpCode(httpCode: HttpCode) {
    addModifier(SetHttpCodeResponseModifier(irisMockScope.chain.hashCode(), httpCode))
}
