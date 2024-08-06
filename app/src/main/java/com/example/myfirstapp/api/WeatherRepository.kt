package com.example.myfirstapp.api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepository {
    fun getWeatherData(cityName: String, apiKey: String, callback: (Result<WeatherResponse>) -> Unit) {
        val call = RetrofitInstance.api.getCurrentWeather(apiKey, cityName)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(Result.success(it))
                    } ?: callback(Result.failure(Exception("Response is empty")))
                } else {
                    callback(Result.failure(Exception("Failed to retrieve weather data")))
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}
