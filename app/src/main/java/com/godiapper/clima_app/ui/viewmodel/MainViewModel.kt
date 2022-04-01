package com.godiapper.clima_app.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godiapper.clima_app.model.WeatherEntity
import com.godiapper.clima_app.repository.RepositoryWeather
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    val WeatherResponse = MutableLiveData<WeatherEntity>()
    val isloading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val repositoryWeather = RepositoryWeather()

    suspend fun getResponse(){
        val call = repositoryWeather.getWeatherById()
        viewModelScope.launch {
            try{
               getWeatherById()
           }catch (e: Exception){
               error.value = e.message
           }
            isloading.value = false
        }
    }
    private suspend fun getWeatherById(){
        val get = repositoryWeather.getWeatherById()
        if (get.isSuccessful){
            WeatherResponse.value = get.body()
        }else{
            error.value = get.message()
        }
    }
}