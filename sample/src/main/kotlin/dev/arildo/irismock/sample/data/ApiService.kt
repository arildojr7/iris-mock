package dev.arildo.irismock.sample.data

import dev.arildo.irismock.sample.data.model.DummyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {
    @Headers("NormalHeader: any")
    @GET("/v1/public/characters")
    suspend fun getUserProfile(): Response<DummyResponse>

}
