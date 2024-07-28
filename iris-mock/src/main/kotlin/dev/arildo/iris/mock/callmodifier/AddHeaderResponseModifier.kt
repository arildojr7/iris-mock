package dev.arildo.iris.mock.callmodifier

import okhttp3.Response

internal data class AddHeaderResponseModifier(
    override val chainHashCode: Int,
    val header: Pair<String, String>,
) : ResponseModifier(chainHashCode) {
    override fun process(response: Response.Builder) {
        response.header(header.first, header.second)
    }
}
