package com.example.foodhub.data.remote

import com.example.foodhub.data.network.FoodApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 10.0.2.2 es tu PC (Localhost) desde el emulador
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val api: FoodApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FoodApi::class.java)
    }
}