package com.example.myfirstapp.api

data class WeatherResponse(
    val location: Location,
    val current: Current
)

data class Location(
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double
)

data class Current(
    val temperature: Int,
    val humidity: Int,
    val weather_descriptions: List<String>
)
