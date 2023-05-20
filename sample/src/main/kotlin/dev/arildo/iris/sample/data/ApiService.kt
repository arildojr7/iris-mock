package dev.arildo.iris.sample.data

import dev.arildo.iris.sample.data.model.DummyResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("/v1/public/characters")
    suspend fun getUserProfile(): Response<DummyResponse>

}
