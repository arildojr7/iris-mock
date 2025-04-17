package dev.arildo.irismock.callmodifier

import okhttp3.Request
import okhttp3.Response

internal sealed class CallModifier(open val chainHashCode: Int)

internal sealed class RequestModifier(
    override val chainHashCode: Int
) : CallModifier(chainHashCode) {
    abstract fun process(request: Request.Builder)
}

internal sealed class ResponseModifier(
    override val chainHashCode: Int
) : CallModifier(chainHashCode) {
    abstract fun process(response: Response.Builder)
}

internal sealed class BehaviourModifier(
    override val chainHashCode: Int
) : CallModifier(chainHashCode)
