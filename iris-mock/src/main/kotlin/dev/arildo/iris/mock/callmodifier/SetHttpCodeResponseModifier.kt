package dev.arildo.iris.mock.callmodifier

import dev.arildo.iris.mock.util.HttpCode
import okhttp3.Response

internal data class SetHttpCodeResponseModifier(
    override val chainHashCode: Int,
    val httpCode: HttpCode,
) : ResponseModifier(chainHashCode) {
    override fun process(response: Response.Builder) {
        response.code(httpCode.code)
    }
}
