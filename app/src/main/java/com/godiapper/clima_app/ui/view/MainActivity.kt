package com.godiapper.clima_app.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import coil.load
import com.godiapper.clima_app.R
import com.godiapper.clima_app.databinding.ActivityMainBinding
import com.godiapper.clima_app.model.WeatherEntity
import com.godiapper.clima_app.model.WeatherService
import com.godiapper.clima_app.utils.checkForInternet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }



   private fun setupViewData() {
        if (checkForInternet(this)){
            lifecycleScope.launch {
            }
        }else {
            showError("Sin acceso a internet")
            binding.linearLayoutHumidity.isVisible = false
            binding.LineraLayoutPressure.isVisible = false
            binding.LinearLayoutWind.isVisible = false
            binding.LinearLayoutInfo.isVisible = false
            binding.textViewAdress.isVisible = false
            binding.textViewActualization.isVisible = false
            binding.textViewMinimum.isVisible = false
            binding.textViewMaxiimum.isVisible = false
            binding.textViewFeeling.isVisible = false
            binding.imageViewStatus.isVisible = false
            binding.textViewTemperature.isVisible = false
        }

    }

    private fun formatResponse(weaterEntity:WeatherEntity){
        try {
            val temp = "${weaterEntity.main.temp.toInt()}°"
            val cityName = weaterEntity.name
            val coutry = weaterEntity.sys.country
            val addres = "$cityName, $coutry"
            val tempMin = "Min: ${weaterEntity.main.temp_min.toInt()}°"
            val tempMax = "Max: ${weaterEntity.main.temp_max.toInt()}°"
            val status = weaterEntity.weather[0].description.uppercase()
            val dt = weaterEntity.dt
            val updateAt = " Actualizacion: ${SimpleDateFormat(
                "hh:mm a", Locale.ENGLISH).format(Date(dt*1000))}"
            val wind = "${weaterEntity.wind.sped} km/hr"
            val pressure = "${weaterEntity.main.pressure} mb"
            val humidity = "${weaterEntity.main.humidity} %"
            val feelsLike = "Sensacion Termica : ${weaterEntity.main.feels_like.toInt()}°"
            val icon = weaterEntity.weather[0].icon
            val iconUrl = "https://openweathermap.org/img/w/$icon.png"

            binding.apply {
                textViewTemperature.text = temp
                textViewAdress.text = addres
                textViewActualization.text = updateAt
                imageViewStatus.load(iconUrl)
                textViewFeeling.text = status
                textViewMaxiimum.text = tempMin
                textViewMinimum.text = tempMax
                textViewWind.text = wind
                textViewPressure.text = pressure
                textViewHumidity.text = humidity
                LinearLayoutWind.isVisible = true
                LinearLayoutInfo.isVisible = true

                LineraLayoutPressure.isVisible = true
                linearLayoutHumidity.isVisible = true
                binding.textViewAdress.isVisible = true
                binding.textViewActualization.isVisible = true
                binding.textViewMinimum.isVisible = true
                binding.textViewMaxiimum.isVisible = true
                binding.textViewFeeling.isVisible = true
                binding.imageViewStatus.isVisible = true
                binding.textViewTemperature.isVisible = true

            }
            shoIndicator(false)
        }catch (exception: Exception){
            Log.e("error format", "Ha ocurrido un error")
        }
    }

    private suspend fun getWeather(): WeatherEntity = withContext(Dispatchers.IO){
        shoIndicator(true)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: WeatherService = retrofit.create(WeatherService::class.java)

        service.getWeatherById(3527879, "metric","sp", "cde500865b040bff958bab839bc60394")
    }

    private fun showError(message: String){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }

    private fun shoIndicator(visible:Boolean){
        binding.progressBarIndicator.isVisible = visible
    }
}