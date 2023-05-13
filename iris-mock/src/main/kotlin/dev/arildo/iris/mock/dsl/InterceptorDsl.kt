@file:JvmMultifileClass
@file:Suppress("unused")
package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.IrisMockCondition
import dev.arildo.iris.mock.IrisMockScope
import dev.arildo.iris.mock.createCondition
import dev.arildo.iris.mock.util.METHOD_GET
import dev.arildo.iris.mock.util.METHOD_POST

fun IrisMockScope.onGet(endsWith: String = "", contains: String = "") =
    onIntercept(endsWith, contains, METHOD_GET)

fun IrisMockScope.onPost(endsWith: String = "", contains: String = "") =
    onIntercept(endsWith, contains, METHOD_POST)

private fun IrisMockScope.onIntercept(
    endsWith: String,
    contains: String,
    method: String
): IrisMockCondition {
    return createCondition(request, contains, endsWith, method).also {
        if (it.shouldIntercept) IrisMockScope.logger.info("Intercepting: [$method] $url")
    }
}
