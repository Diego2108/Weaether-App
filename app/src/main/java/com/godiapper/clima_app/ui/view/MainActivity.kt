package com.godiapper.clima_app.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import coil.load
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

        setupViewData()
    }

    private fun setupViewData() {
        if (checkForInternet(this)){
            lifecycleScope.launch {
                formatResponse(getWeather())
            }
        }else {
            showError("Sin acceso a internet")
           /* binding.cardviewAmanecer.isVisible = false
            binding.cardviewAtardecer.isVisible = false*/
            binding.linearLayoutHumidity.isVisible = false
            binding.LineraLayoutPressure.isVisible = false
            binding.LinearLayoutWind.isVisible = false
            binding.LinearLayoutInfo.isVisible = false
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
           /* val sunrise = weaterEntity.sys.sunrise
            val sunriseFormat = SimpleDateFormat("hh:mm a",
                Locale.ENGLISH).format(Date(sunrise*1000))
            val sunset = weaterEntity.sys.sunset
            val sunsetFormat = SimpleDateFormat("hh:mm a",
                Locale.ENGLISH).format(Date(sunset*1000))*/
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
                /*textViewsunrise.text = sunriseFormat
                textViewSunset.text = sunsetFormat*/
                textViewWind.text = wind
                textViewPressure.text = pressure
                textViewHumidity.text = humidity
                /*tvCielo.text = feelsLike
                cardviewAmanecer.isVisible = true
                cardviewAtardecer.isVisible = true*/
                LinearLayoutWind.isVisible = true
                LinearLayoutInfo.isVisible = true
                LineraLayoutPressure.isVisible = true
                linearLayoutHumidity.isVisible = true

            }
            shoIndicator(false)
        }catch (exception: Exception){
            Log.e("error format", "Ha ocurrido un error")
        }
    }

    /*private fun formatResponse (weatherEntity: WeatherEntity){
        try{
            val temp = "${weatherEntity.main.temp.toInt()}°"
            val cityName = weatherEntity.name
            val country = weatherEntity.sys.country
            val address = "$cityName,$country"
            val dateNow = Calendar.getInstance().time
            val tempMin ="Min ${weatherEntity.main.temp_min.toInt()}°"
            val tempMax = "Max ${weatherEntity.main.temp_max.toInt()}°"
            val feel = "Sensacion: ${weatherEntity.main.feels_like.toInt()}°"
            val status = weatherEntity.weather[0].description.uppercase()

            binding.apply {
                ubicacion.text= address
                tvFecha.text = dateNow.toString()
                tvTemperatura.text = temp
                tvCielo.text = feel
                tvMinima.text = tempMin
                tvMaxima.text = tempMax
                tvStatus.text = status
            }

        }catch (exception:Exception){
            // showError("Ha ocurrido un error")
            Log.e("error format", "Ha ocurrido un error")
        }
    }*/

    private suspend fun getWeather(): WeatherEntity = withContext(Dispatchers.IO){
        shoIndicator(true)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: WeatherService = retrofit.create(WeatherService::class.java)

        service.getWeatherById(3527879, "metric","sp", "cde500865b040bff958bab839bc60394")
    }

    /* private fun setupTitle(newTitle: String) {
         supportActionBar?.let { title = newTitle }
     }*/

    private fun showError(message: String){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }

    private fun shoIndicator(visible:Boolean){
        binding.progressBarIndicator.isVisible = visible
    }
}