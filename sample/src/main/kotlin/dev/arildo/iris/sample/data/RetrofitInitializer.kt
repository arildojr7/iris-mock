package dev.arildo.iris.sample.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInitializer {
    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS).build()
    }
    private fun createNetworkClient(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(createOkHttpClient())
        .build()
    fun apiService(): ApiService = createNetworkClient().create(ApiService::class.java)

    const val TIMEOUT: Long = 10
    const val BASE_URL = "https://gateway.marvel.com"
}