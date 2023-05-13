@file:JvmMultifileClass
@file:Suppress("unused")
package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.IrisMockCondition
import dev.arildo.iris.mock.IrisMockScope
import dev.arildo.iris.mock.util.SUCCESS_CODE

infix fun IrisMockCondition.mockResponse(response: String) {
    if (shouldIntercept) {
        IrisMockScope.logger.info("Mocking Response: [$method] ${irisMockScope.url}")
        irisMockScope.createCustomResponse(SUCCESS_CODE, response)
    }
}

infix fun IrisMockCondition.then(block: IrisMockCondition.() -> Unit) {
    if (shouldIntercept) block(this)
}
