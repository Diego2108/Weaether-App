package com.godiapper.clima_app.model.weather

import com.godiapper.clima_app.model.Weather


data class Current (
    val dt: Long,
    val temp: Double,
    val feels_like: Double,
    val wind_speed: Double,
    val pressure: Int,
    val humidity: Int,
    val weather: List<Weather>
)