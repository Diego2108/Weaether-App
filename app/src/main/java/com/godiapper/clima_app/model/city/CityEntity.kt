package com.godiapper.clima_app.model.city

import com.godiapper.clima_app.model.weather.Current

data class CityEntity(
    val current: Current,
    val city: City?
)
