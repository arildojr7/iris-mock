package dev.arildo.irismock.callmodifier

import okhttp3.Request

internal data class RemoveHeaderRequestModifier(
    override val chainHashCode: Int,
    val headerKey: String,
) : RequestModifier(chainHashCode) {
    override fun process(request: Request.Builder) {
        request.removeHeader(headerKey)
    }
}
