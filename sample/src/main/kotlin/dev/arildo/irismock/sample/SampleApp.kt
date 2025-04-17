package dev.arildo.irismock.sample

import android.app.Application
import dev.arildo.irismock.dsl.enableLogs
import dev.arildo.irismock.dsl.interceptors
import dev.arildo.irismock.dsl.startIrisMock

class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startIrisMock {
            enableLogs()
            interceptors(
                ::SampleInterceptor,
            )
        }
    }
}
