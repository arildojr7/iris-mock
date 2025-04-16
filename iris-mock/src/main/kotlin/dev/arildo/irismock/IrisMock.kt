package dev.arildo.irismock

import dev.arildo.irismock.util.IRIS_MOCK_TAG
import okhttp3.Interceptor
import java.util.logging.Logger
import kotlin.reflect.KFunction0

object IrisMock {
    internal var enableLogs = false
    internal val interceptors = mutableListOf<KFunction0<Interceptor>>()

    internal val logger = Logger.getLogger(IRIS_MOCK_TAG)
}
