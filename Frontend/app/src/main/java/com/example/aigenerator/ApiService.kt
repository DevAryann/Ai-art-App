package com.example.aigenerator

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// 1. What we send TO the server
data class GenerateRequest(
    val prompt: String
)

// 2. What we get FROM the server
data class GenerateResponse(
    val image_url: String,
    val status: String
)

interface ApiService {
    @GET("/")
    suspend fun checkHealth(): Map<String, String> // Simple health check

    // The new Endpoint!
    @POST("generate")
    suspend fun generateImage(@Body request: GenerateRequest): GenerateResponse
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5000/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}