package dev.arildo.irismock

import okhttp3.Interceptor
import java.util.logging.Logger

private const val IRIS_MOCK_TAG = "IrisMock"

object IrisMock {
    internal var enableLogs = false
    internal val interceptors = mutableListOf<() -> Interceptor>()

    internal val logger = Logger.getLogger(IRIS_MOCK_TAG)
}
