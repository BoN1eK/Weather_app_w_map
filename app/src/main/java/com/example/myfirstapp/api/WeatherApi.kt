package com.example.myfirstapp.api

import com.example.myfirstapp.model.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("current")
    fun getCurrentWeather(
        @Query("access_key") apiKey: String,
        @Query("query") city: String
    ): Call<WeatherResponse>
}
