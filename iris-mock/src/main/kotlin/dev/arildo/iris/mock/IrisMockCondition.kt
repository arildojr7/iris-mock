package dev.arildo.iris.mock

import okhttp3.Request

open class IrisMockCondition internal constructor(
    internal val method: String,
    internal val shouldIntercept: Boolean,
    internal val irisMockScope: IrisMockScope
)

internal fun IrisMockScope.createCondition(
    request: Request,
    contains: String,
    endsWith: String,
    method: String
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
                url.endsWith(endsWith, true) && chainMethod == method
            }

            contains.isNotBlank() -> {
                url.contains(contains, true) && chainMethod == method
            }

            else -> throw IllegalArgumentException("Must provide a string to match on url")
        }
    )
}
