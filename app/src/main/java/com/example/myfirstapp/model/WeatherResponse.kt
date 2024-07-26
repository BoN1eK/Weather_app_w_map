package com.example.myfirstapp.model

data class WeatherResponse(
    val current: Current,
    val location: Location
)

data class Current(
    val temperature: Int,
    val weather_descriptions: List<String>,
    val humidity: Int
)

data class Location(
    val name: String,
    val country: String
)
