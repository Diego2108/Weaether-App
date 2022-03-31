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

    suspend fun getResponse(weather:String){
        val call = repositoryWeather.getWeatherById(weather)
        viewModelScope.launch {
           try{
               WeatherResponse.postValue(call.body())
               isloading.value = true
           }catch (e: Exception){
               error.value = e.message
           }
            isloading.value = false
        }
    }
}