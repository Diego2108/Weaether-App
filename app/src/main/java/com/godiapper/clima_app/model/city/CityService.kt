package com.godiapper.clima_app.model.city

import retrofit2.http.GET
import retrofit2.http.Query

interface CityService {
    @GET("geo/1.0/reverse")
    suspend fun getCitiesByLating(
        @Query("lat") lat:String,
        @Query("lon") lon:String,
        @Query("appid") appid:String
    ): List<City>
}