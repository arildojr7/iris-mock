package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.IrisMockCondition
import dev.arildo.iris.mock.IrisMockScope

/**
 * This scope class holds everything that can be used only inside `then {}` block
 */
class InterceptedScope(method: String, shouldIntercept: Boolean, irisMockScope: IrisMockScope) :
    IrisMockCondition(method, shouldIntercept, irisMockScope)

fun InterceptedScope.addHeader(header: Pair<String, Any?>) {
    // todo
}

fun InterceptedScope.removeHeader(header: Pair<String, Any?>) {
    // todo
}
