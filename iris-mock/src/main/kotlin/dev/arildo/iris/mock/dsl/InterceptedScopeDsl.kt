@file:JvmMultifileClass
@file:Suppress("unused")

package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.InterceptedScope

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
