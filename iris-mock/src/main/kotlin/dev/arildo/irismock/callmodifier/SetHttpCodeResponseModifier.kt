package dev.arildo.irismock.callmodifier

import dev.arildo.irismock.callmodifier.ResponseModifier
import dev.arildo.irismock.util.HttpCode
import okhttp3.Response

internal data class SetHttpCodeResponseModifier(
    override val chainHashCode: Int,
    val httpCode: HttpCode,
) : ResponseModifier(chainHashCode) {
    override fun process(response: Response.Builder) {
        response.code(httpCode.code)
    }
}
