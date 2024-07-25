package dev.arildo.iris.mock.callmodifier

import okhttp3.Request

internal data class AddHeaderRequestModifier(
    override val chainHashCode: Int,
    val header: Pair<String, String>,
) : RequestModifier(chainHashCode) {
    override fun process(request: Request.Builder) {
        request.addHeader(header.first, header.second)
    }
}
