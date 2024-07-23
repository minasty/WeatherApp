package com.example.weatherapp2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private val weatherBaseUrl = "https://api.weatherbit.io/v2.0/current"
    private val apiKey = "fdd80dbf17354b36a43ebc5f15686d98"

    private lateinit var textView: TextView
    private lateinit var btVar1: Button
    private lateinit var cityInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        btVar1 = findViewById(R.id.btVar1)
        cityInput = findViewById(R.id.cityInput)

        btVar1.setOnClickListener {
            val city = cityInput.text.toString()
            if (city.isNotEmpty()) {
                getWeatherData(city)
            } else {
                textView.text = "Please enter a city name."
            }
        }
    }

    private fun getWeatherData(city: String) {
        val weatherUrl = "$weatherBaseUrl?city=$city&key=$apiKey"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(Request.Method.GET, weatherUrl,
            Response.Listener<String> { response ->
                parseWeatherData(response)
            },
            Response.ErrorListener {
                textView.text = "Failed to retrieve weather data."
                Log.e("WeatherError", "Failed to retrieve weather data", it)
            })

        queue.add(stringRequest)
    }

    private fun parseWeatherData(response: String) {
        try {
            val jsonObject = JSONObject(response)
            val dataArray = jsonObject.getJSONArray("data")
            val weatherData = dataArray.getJSONObject(0)

            val temperature = weatherData.getString("temp")
            val cityName = weatherData.getString("city_name")
            val humidity = weatherData.getString("rh")
            val pressure = weatherData.getString("pres")
            val precipitation = weatherData.optString("precip", "N/A")
            val airQuality = weatherData.optString("aqi", "N/A") // Assuming "aqi" is the key for air quality index
            val description = weatherData.getJSONObject("weather").getString("description")

            textView.text = "Temperature: $temperature°C\nHumidity: $humidity%\nPressure: $pressure hPa\nPrecipitation: $precipitation mm\nAir Quality: $airQuality\nDescription: $description\nCity: $cityName"
        } catch (e: Exception) {
            textView.text = "Error parsing weather data."
            Log.e("ParseError", "Error parsing weather data", e)
        }
    }
}
