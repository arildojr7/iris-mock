package dev.arildo.iris.mock

import dev.arildo.iris.mock.util.Method
import okhttp3.Request

open class IrisMockCondition internal constructor(
    internal val method: Method,
    internal val shouldIntercept: Boolean,
    internal val irisMockScope: IrisMockScope
)

internal fun IrisMockScope.createCondition(
    request: Request,
    contains: String,
    endsWith: String,
    method: Method
): IrisMockCondition {
    val url = request.url().toString()
    val chainMethod = request.method()

    return IrisMockCondition(
        method = method,
        irisMockScope = this,
        shouldIntercept = when {
            endsWith.isNotBlank() && contains.isNotBlank() -> {
                throw IllegalArgumentException("Must provide only one string matcher")
            }

            endsWith.isNotBlank() -> {
                url.endsWith(endsWith, true) && chainMethod == method.name
            }

            contains.isNotBlank() -> {
                url.contains(contains, true) && chainMethod == method.name
            }

            else -> throw IllegalArgumentException("Must provide a string to match on url")
        }
    )
}
