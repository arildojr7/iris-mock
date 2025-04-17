package dev.arildo.irismock.callmodifier

import okhttp3.Response

internal data class RemoveHeaderResponseModifier(
    override val chainHashCode: Int,
    val headerKey: String,
) : ResponseModifier(chainHashCode) {
    override fun process(response: Response.Builder) {
        response.removeHeader(headerKey)
    }
}
