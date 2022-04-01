package com.godiapper.clima_app.network

import com.godiapper.clima_app.model.WeatherService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherInstance {

    fun getWeather(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}