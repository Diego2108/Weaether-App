package com.godiapper.clima_app.model

data class WeatherEntity(
    val base: String,
    val main: Main,
    val sys: Sys,
    val id: Int,
    val name: String,
    val wind: Wind,
    val weather: List<Weather>,
    val dt: Long

)
