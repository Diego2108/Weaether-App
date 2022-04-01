package com.godiapper.clima_app.repository

import com.godiapper.clima_app.model.WeatherEntity
import com.godiapper.clima_app.model.WeatherService
import com.godiapper.clima_app.network.WeatherInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create

class RepositoryWeather {

    val retrofit = WeatherInstance.getWeather().create(WeatherService::class.java)

    suspend fun getWeatherById(): Response<WeatherEntity> = withContext(Dispatchers.IO){
        retrofit.getWeatherById(3527879,"metric","sp","cde500865b040bff958bab839bc60394")
    }
}