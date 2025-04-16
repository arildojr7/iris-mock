@file:JvmMultifileClass
@file:Suppress("unused")

package dev.arildo.irismock.dsl

import dev.arildo.irismock.callmodifier.AddHeaderRequestModifier
import dev.arildo.irismock.callmodifier.AddHeaderResponseModifier
import dev.arildo.irismock.callmodifier.RemoveHeaderRequestModifier
import dev.arildo.irismock.callmodifier.RemoveHeaderResponseModifier
import dev.arildo.irismock.InterceptedRequest

/**
 * Add header to the original/modified request
 */
fun InterceptedRequest.addHeaderRequest(header: Pair<String, String>) {
    addModifier(AddHeaderRequestModifier(irisMockScope.chain.hashCode(), header))
}

/**
 * Remove header from the original/modified request
 */
fun InterceptedRequest.removeHeaderRequest(headerKey: String) {
    addModifier(RemoveHeaderRequestModifier(irisMockScope.chain.hashCode(), headerKey))
}

/**
 * Add header to the original/modified response
 */
fun InterceptedRequest.addHeaderResponse(header: Pair<String, String>) {
    addModifier(AddHeaderResponseModifier(irisMockScope.chain.hashCode(), header))
}

/**
 * Remove header from the original/modified response
 */
fun InterceptedRequest.removeHeaderResponse(headerKey: String) {
    addModifier(RemoveHeaderResponseModifier(irisMockScope.chain.hashCode(), headerKey))
}
