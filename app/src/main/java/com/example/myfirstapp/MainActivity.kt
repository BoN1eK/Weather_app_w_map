package com.example.myfirstapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirstapp.api.RetrofitInstance
import com.example.myfirstapp.model.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var editTextCity: EditText
    private lateinit var buttonSubmit: Button
    private lateinit var textViewResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextCity = findViewById(R.id.editTextCity)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        textViewResult = findViewById(R.id.textViewResult)

        buttonSubmit.setOnClickListener {
            val cityName = editTextCity.text.toString()

            if (cityName.isNotEmpty()) {
                getWeatherData(cityName)
            } else {
                textViewResult.text = "Wprowadź nazwę miasta."
            }
        }
    }

    private fun getWeatherData(cityName: String) {
        val apiKey = "639b064f1e2982d599b648584228070e"
        val call = RetrofitInstance.api.getCurrentWeather(apiKey, cityName)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    weatherResponse?.let {
                        val weatherInfo = """
                            Location: ${it.location.name}, ${it.location.country}
                            Temperature: ${it.current.temperature}°C
                            Humidity: ${it.current.humidity}%
                            Description: ${it.current.weather_descriptions[0]}
                        """.trimIndent()
                        textViewResult.text = weatherInfo
                    }
                } else {
                    textViewResult.text = "Response not successful: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                textViewResult.text = "Failed to get weather data: ${t.message}"
            }
        })
    }
}