package dev.arildo.irismock.util

import dev.arildo.irismock.IrisMock
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import okio.buffer

const val APPLICATION_JSON = "application/json"

internal fun Response.log() {
    val body = getResponseBody(this)
    val protocol = formatProtocol(protocol())
    val statusCode = HttpCode.getEnum(code())
    val requestBody = formatRequestBody(request())

    IrisMock.logger.info(
        """
[${request().method()}] => ${request().url()}
${request().headers()}$requestBody--- end request ---

<= ${code()} $statusCode - $protocol (${receivedResponseAtMillis() - sentRequestAtMillis()}ms)
${headers()}Body: $body
--- end response ---
"""
    )
}

internal fun readRequestBody(request: Request) = Buffer().also {
    // TODO verify if it behaves like response body, consuming it when accessed
    request.newBuilder().build().body()?.writeTo(it)
}.readUtf8()

private fun formatProtocol(protocol: Protocol) =
    protocol.name.replaceFirst("_", "/").replace('_', '.')

private fun formatRequestBody(request: Request) =
    readRequestBody(request).takeIf { it.isNotBlank() }?.let { "Body: $it\n" }.orEmpty()

private fun getResponseBody(response: Response): String {
    val requestBody = response.peekBody(Long.MAX_VALUE)
    val body = requestBody.run {
        if (response.header("content-encoding") == "gzip") {
            val buffer = GzipSource(source()).buffer()
            buffer.readUtf8()
        } else {
            string()
        }
    }.orEmpty()

    return body
}
