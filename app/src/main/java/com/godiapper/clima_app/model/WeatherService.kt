package com.godiapper.clima_app.model

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    // api.openweathermap.org/data/2.5/onecall?lat={lat}&lon={lon}&exclude={part}&appid={API key}
    @GET("data/2.5/onecall")
    suspend fun getWeatherById(
        @Query("lat") lat: String,
        @Query("lon") lon: Long,
        @Query("units") units: String?,
        @Query("lang") lang: String?,
        @Query("appid") appid: String?): WeatherEntity
}