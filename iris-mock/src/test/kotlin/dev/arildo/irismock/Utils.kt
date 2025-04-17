package dev.arildo.irismock

import dev.arildo.irismock.util.APPLICATION_JSON
import dev.arildo.irismock.util.HttpCode
import okhttp3.Interceptor.Chain
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody

internal object Utils {
    fun createBlankResponse(chain: Chain): Response = Response.Builder()
        .request(chain.request())
        .protocol(Protocol.HTTP_1_1)
        .code(HttpCode.OK.code)
        .body(ResponseBody.create(MediaType.get(APPLICATION_JSON), ""))
        .message("")
        .build()
}