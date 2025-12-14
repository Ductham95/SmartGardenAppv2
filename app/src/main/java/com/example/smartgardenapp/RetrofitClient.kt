package com.example.smartgardenapp

// --- Retrofit Singleton ---
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://eu.thingsboard.cloud" // URL server của bạn

    val instance: ThingsBoardApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ThingsBoardApi::class.java)
    }
}