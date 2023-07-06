@file:JvmMultifileClass
@file:Suppress("unused", "UnusedReceiverParameter")

package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.IrisMockCondition
import dev.arildo.iris.mock.IrisMockScope
import dev.arildo.iris.mock.util.Method

/**
 * This scope class holds everything that can be used only inside `then {}` block
 */
class InterceptedScope(method: Method, shouldIntercept: Boolean, irisMockScope: IrisMockScope) :
    IrisMockCondition(method, shouldIntercept, irisMockScope)

fun InterceptedScope.addHeaderRequest(header: Pair<String, Any?>) {
    irisMockScope.customRequest.addHeader(header.first, header.second.toString())
}

fun InterceptedScope.removeHeaderRequest(headerKey: String) {
    irisMockScope.customRequest.removeHeader(headerKey)
}

fun InterceptedScope.addHeaderResponse(header: Pair<String, Any?>) {
    irisMockScope.customResponse.addHeader(header.first, header.second.toString())
}

fun InterceptedScope.removeHeaderResponse(headerKey: String) {
    irisMockScope.customResponse.removeHeader(headerKey)
}
