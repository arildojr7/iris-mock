package dev.arildo.iris.mock.util

import dev.arildo.iris.mock.IrisMockScope
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import okio.GzipSource
import okio.buffer

internal fun readRequestBody(request: Request) = Buffer().also {
    request.newBuilder().build().body()?.writeTo(it)
}.readUtf8()

fun Response.log(): Response {
    val (newResponse, body) = getResponseBodyAndNewResponse(this)
    val protocol = formatProtocol(protocol())
    val codeDescription = HttpCode.getEnum(code())
    val requestBody = formatRequestBody(request())

    IrisMockScope.logger.info(
        """
[${request().method()}] => ${request().url()}
${request().headers()}$requestBody--- end request ---

<= ${code()} $codeDescription - $protocol (${receivedResponseAtMillis() - sentRequestAtMillis()}ms)
${headers()}Body: $body
--- end response ---
"""
    )
    return newResponse
}

private fun formatProtocol(protocol: Protocol) =
    protocol.name.replaceFirst("_", "/").replace('_', '.')

private fun formatRequestBody(request: Request) =
    readRequestBody(request).takeIf { it.isNotBlank() }?.let { "Body: $it\n" }.orEmpty()

private fun getResponseBodyAndNewResponse(response: Response): Pair<Response, String> {
    // since body() is erased when its value is read, we need to recreate its builder
    var newResponse = response.newBuilder()
    val contentType = response.body()?.contentType()
    val body = response.body()?.run {
        if (response.header("content-encoding") == "gzip") {
            newResponse = newResponse.removeHeader("content-encoding")
            val buffer = GzipSource(source()).buffer()
            buffer.readUtf8()
        } else {
            string()
        }
    }.orEmpty()

    val wrappedBody = ResponseBody.create(contentType, body)
    return newResponse.body(wrappedBody).build() to body
}
