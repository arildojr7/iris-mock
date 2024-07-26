@file:JvmMultifileClass
@file:Suppress("unused")

package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.InterceptedRequest
import dev.arildo.iris.mock.callmodifier.AddHeaderRequestModifier
import dev.arildo.iris.mock.callmodifier.AddHeaderResponseModifier
import dev.arildo.iris.mock.callmodifier.RemoveHeaderRequestModifier
import dev.arildo.iris.mock.callmodifier.RemoveHeaderResponseModifier

/**
 * Add a header to the original/modified request
 */
fun InterceptedRequest.addHeaderRequest(header: Pair<String, String>) {
    addModifier(AddHeaderRequestModifier(irisMockScope.chain.hashCode(), header))
}

/**
 * Remove a header to the original/modified request
 */
fun InterceptedRequest.removeHeaderRequest(headerKey: String) {
    addModifier(RemoveHeaderRequestModifier(irisMockScope.chain.hashCode(), headerKey))
}

/**
 * Add a header to the original/modified response
 */
fun InterceptedRequest.addHeaderResponse(header: Pair<String, String>) {
    addModifier(AddHeaderResponseModifier(irisMockScope.chain.hashCode(), header))
}

/**
 * Remove a header to the original/modified response
 */
fun InterceptedRequest.removeHeaderResponse(headerKey: String) {
    addModifier(RemoveHeaderResponseModifier(irisMockScope.chain.hashCode(), headerKey))
}
