package dev.arildo.iris.mock.util

import dev.arildo.iris.mock.IrisMockScope
import okhttp3.Request
import okhttp3.Response
import okio.Buffer

internal fun readRequestBody(request: Request) = Buffer().also {
    request.newBuilder().build().body()?.writeTo(it)
}.readUtf8()

internal fun Response.log() {
    val protocol = protocol().name.replaceFirst("_", "/").replace('_', '.')
    IrisMockScope.logger.info(
        """IrisMock [${request().method()}] ${request().url()}
$protocol ${code()}
Headers:
${headers()}
RequestBody:
${readRequestBody(request())}
"""
    )
}
