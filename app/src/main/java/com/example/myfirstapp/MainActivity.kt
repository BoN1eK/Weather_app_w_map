package com.example.myfirstapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myfirstapp.api.WeatherRepository
import com.example.myfirstapp.api.WeatherResponse
import com.example.myfirstapp.api.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var editTextCity: EditText
    private lateinit var buttonSubmit: Button
    private lateinit var buttonLoc: Button
    private lateinit var textViewResult: TextView
    private lateinit var map: MapView

    private val weatherRepository = WeatherRepository()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Configuration.getInstance().userAgentValue = packageName

        map = findViewById(R.id.map)
        map.setMultiTouchControls(true)

        editTextCity = findViewById(R.id.editTextCity)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        buttonLoc = findViewById(R.id.buttonLoc)
        textViewResult = findViewById(R.id.textViewResult)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        buttonLoc.setOnClickListener {
            if (checkPermissions()) {
                getCurrentLocation()
            } else {
                requestPermissions()
            }
        }

        buttonSubmit.setOnClickListener {
            val cityName = editTextCity.text.toString()

            if (cityName.isNotEmpty()) {
                getWeatherData(cityName)
                hideKeyboard()
            } else {
                textViewResult.text = getString(R.string.enter_city_name)
            }
        }

        editTextCity.setOnClickListener {
            toggleKeyboard(editTextCity)
        }
    }

    private fun toggleKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive(view)) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } else {
            view.requestFocus()
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun checkPermissions(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fineLocation == PackageManager.PERMISSION_GRANTED && coarseLocation == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                textViewResult.text = getString(R.string.permission_denied)
            }
        }
    }

    private fun getCurrentLocation() {
        try {
            if (checkPermissions()) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            latitude = it.latitude
                            longitude = it.longitude
                            getCityNameFromLocation(latitude!!, longitude!!)
                        } ?: run {
                            textViewResult.text = getString(R.string.failed_to_get_location)
                        }
                    }
            } else {
                requestPermissions()
            }
        } catch (e: SecurityException) {
            textViewResult.text = getString(R.string.permission_denied)
        }
    }

    private fun getCityNameFromLocation(latitude: Double, longitude: Double) {
        val apiKey = getString(R.string.weather_api)
        val locationQuery = "$latitude,$longitude"

        Log.d("API_KEY", "Using API Key: $apiKey")

        val call = RetrofitInstance.api.getCurrentWeather(apiKey, locationQuery)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val cityName = response.body()?.location?.name
                    cityName?.let {
                        getWeatherData(it)
                    } ?: run {
                        Log.e("API_ERROR", "City name is null")
                        textViewResult.text = getString(R.string.failed_to_get_city_name)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Response code: ${response.code()}, Error: $errorBody")
                    textViewResult.text = getString(R.string.failed_to_get_city_name)
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("API_ERROR", "Request failed", t)
                textViewResult.text = getString(R.string.failed_to_get_city_name)
            }
        })
    }



    private fun getWeatherData(cityName: String) {
        val apiKey = getString(R.string.weather_api)

        weatherRepository.getWeatherData(cityName, apiKey) { result ->
            result.onSuccess { weatherResponse ->
                try {
                    val weatherInfo = getString(
                        R.string.weather_info,
                        weatherResponse.location.name,
                        weatherResponse.location.country,
                        weatherResponse.current.temperature,
                        weatherResponse.current.humidity,
                        weatherResponse.current.weather_descriptions[0]
                    )

                    map.controller.setZoom(15.0)
                    val startPoint =
                        GeoPoint(weatherResponse.location.lat, weatherResponse.location.lon)
                    map.controller.setCenter(startPoint)

                    val startMarker = Marker(map)
                    startMarker.position = startPoint
                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    map.overlays.add(startMarker)

                    textViewResult.text = weatherInfo
                }catch (e: Exception){
                    textViewResult.text = getString(R.string.failed_to_get_weather_data)
                }
            }.onFailure { exception ->
                textViewResult.text = getString(R.string.failed_to_get_weather_data, exception.message)
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}
