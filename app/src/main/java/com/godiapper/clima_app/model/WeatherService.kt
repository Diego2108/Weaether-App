package com.godiapper.clima_app.model

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getWeatherById(
        @Query("id") lon: Long,
        @Query("units") units: String?,
        @Query("lang") lang: String?,
        @Query("appid") appid: String?): Response<WeatherEntity>
}