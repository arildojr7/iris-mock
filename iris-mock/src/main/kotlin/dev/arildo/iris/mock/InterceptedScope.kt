package dev.arildo.iris.mock

import dev.arildo.iris.mock.util.Method

/**
 * This scope class holds everything that can be used only inside `then {}` block
 */
class InterceptedScope(method: Method, shouldIntercept: Boolean, irisMockScope: IrisMockScope) :
    IrisMockCondition(method, shouldIntercept, irisMockScope)
