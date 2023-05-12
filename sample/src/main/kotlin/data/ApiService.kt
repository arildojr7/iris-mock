package dev.arildo.iris.sample.data

import dev.arildo.iris.sample.data.model.DummyRequest
import dev.arildo.iris.sample.data.model.DummyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("mob/sso/you")
    suspend fun getUserProfile(@Body request: DummyRequest): Response<DummyResponse>

}
