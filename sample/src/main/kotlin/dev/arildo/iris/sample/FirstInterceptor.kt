package dev.arildo.iris.sample

import android.util.Log
import dev.arildo.iris.mock.annotation.IrisMockInterceptor
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

@IrisMockInterceptor
class FirstInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.e(">>>> ", "IT'S WORKING")
        return Response.Builder().request(Request.Builder().build()).build()
    }

}
