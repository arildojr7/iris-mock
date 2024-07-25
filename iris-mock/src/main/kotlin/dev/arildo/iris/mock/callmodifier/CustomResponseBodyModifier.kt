package dev.arildo.iris.mock.callmodifier

import dev.arildo.iris.mock.util.APPLICATION_JSON
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody

internal data class CustomResponseBodyModifier(
    override val chainHashCode: Int,
    val body: String,
) : ResponseModifier(chainHashCode) {
    override fun process(response: Response.Builder) {
        response.message(body).body(ResponseBody.create(MediaType.get(APPLICATION_JSON), body))
    }
}
