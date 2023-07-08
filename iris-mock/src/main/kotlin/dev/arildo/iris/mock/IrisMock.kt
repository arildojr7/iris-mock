package dev.arildo.iris.mock

import okhttp3.Interceptor

object IrisMock {
    internal const val headerPlaceholder = "IRIS_MOCK"
    val interceptors = mutableListOf<Interceptor>()
    var enableLogs = true
        internal set

    fun addInterceptor(interceptor: Interceptor) {
        println("@@@ adding interceptor")
        interceptors.add(interceptor)
    }
}
