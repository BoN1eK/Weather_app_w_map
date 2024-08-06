package com.example.myfirstapp.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("current")
    fun getCurrentWeather(
        @Query("access_key") apiKey: String,
        @Query("query") location: String
    ): Call<WeatherResponse>
}
