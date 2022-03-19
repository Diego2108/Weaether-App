package com.godiapper.clima_app.ui.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import coil.load
import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.PermissionChecker
import com.godiapper.clima_app.BuildConfig
import com.godiapper.clima_app.R
import com.godiapper.clima_app.databinding.ActivityMainBinding
import com.godiapper.clima_app.model.WeatherEntity
import com.godiapper.clima_app.model.WeatherService
import com.godiapper.clima_app.model.city.City
import com.godiapper.clima_app.model.city.CityService
import com.godiapper.clima_app.utils.checkForInternet
import com.godiapper.clima_app.utils.snackBarShow
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private  val TAG = "MainActivityError"

    private lateinit var binding: ActivityMainBinding
    private  val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private var latitude = ""
    private var longitude = ""

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewData()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupLocation()
    }
    private fun setupLocation(){
        if (checkPermission()){
            getLastLocation()
        } else{
            requestPermissions()
        }
    }

    private fun setupViewData(location:Location) {
        if (checkForInternet(this)){
            lifecycleScope.launch {
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
                formatResponse(getWeather())
            }
        }else {
            showError("Sin acceso a internet")
            binding.linearLayoutHumidity.isVisible = false
            binding.LineraLayoutPressure.isVisible = false
            binding.LinearLayoutWind.isVisible = false
            binding.LinearLayoutInfo.isVisible = false
        }

    }

    private fun formatResponse(weaterEntity:WeatherEntity){
        try {
            val temp = "${weaterEntity.main.temp.toInt()}°"
            val cityName = weaterEntity.name
            val coutry = weaterEntity.sys.country
            val addres = "$cityName, $coutry"
            val tempMin = "Min: ${weaterEntity.main.temp_min.toInt()}°"
            val tempMax = "Max: ${weaterEntity.main.temp_max.toInt()}°"
            val status = weaterEntity.weather[0].description.uppercase()
            val dt = weaterEntity.dt
            val updateAt = " Actualizacion: ${SimpleDateFormat(
                "hh:mm a", Locale.ENGLISH).format(Date(dt*1000))}"
            val wind = "${weaterEntity.wind.sped} km/hr"
            val pressure = "${weaterEntity.main.pressure} mb"
            val humidity = "${weaterEntity.main.humidity} %"
            val feelsLike = "Sensacion Termica : ${weaterEntity.main.feels_like.toInt()}°"
            val icon = weaterEntity.weather[0].icon
            val iconUrl = "https://openweathermap.org/img/w/$icon.png"

            binding.apply {
                textViewTemperature.text = temp
                textViewAdress.text = addres
                textViewActualization.text = updateAt
                imageViewStatus.load(iconUrl)
                textViewFeeling.text = status
                textViewMaxiimum.text = tempMin
                textViewMinimum.text = tempMax
                textViewWind.text = wind
                textViewPressure.text = pressure
                textViewHumidity.text = humidity
                LinearLayoutWind.isVisible = true
                LinearLayoutInfo.isVisible = true
                LineraLayoutPressure.isVisible = true
                linearLayoutHumidity.isVisible = true

            }
            shoIndicator(false)
        }catch (exception: Exception){
            Log.e("error format", "Ha ocurrido un error")
        }
    }


    private suspend fun getWeather(): WeatherEntity = withContext(Dispatchers.IO){
        shoIndicator(true)

        var unit = "metric"
        var lenguage = "es"

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: WeatherService = retrofit.create(WeatherService::class.java)

        service.getWeatherById(latitude,longitude,unit,lenguage,"efa318dbab126611b3925839a51ed422")
    }

    private fun checkPermission():Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    private fun startLocationPermissionRequest(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(onLocation: ((location: Location)-> Unit)? = this::setupViewData){
        fusedLocationClient.lastLocation
            .addOnCompleteListener { taskLocation ->
                if (taskLocation.isSuccessful && taskLocation.result != null) {

                    val location = taskLocation.result

                    latitude = location?.latitude.toString()
                    longitude = location?.longitude.toString()
                    Log.d(TAG, "GetLasLoc Lat: $latitude Long: $longitude")

                    onLocation(taskLocation.result)
                } else {
                    Log.w(TAG, "getLastLocation:exception", taskLocation.exception)
                    showSnackbar(R.string.no_location_detected)
                }
            }
    }

    private fun requestPermissions(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            snackBarShow(
                findViewById(android.R.id.content),
                getString(R.string.location_request_indispensable),
                getString(R.string.request)
            ) {
                startLocationPermissionRequest()
            }
        } else {
            startLocationPermissionRequest()
        }
    }

    private fun showError(message: String){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }

    private fun shoIndicator(visible:Boolean){
        binding.progressBarIndicator.isVisible = visible
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_PERMISSIONS_REQUEST_CODE){
            when {
                grantResults.isEmpty() -> Log.i("ON_REQUEST_PERM", "interaccion del usuario cancelada")
                grantResults.first() == PackageManager.PERMISSION_GRANTED -> getLastLocation()
                else -> {
                    snackBarShow(
                        findViewById(android.R.id.content),
                        getString(R.string.permission_denied),
                        getString(R.string.open_settings)
                    ) {
                        Intent().apply{
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    }
                }
            }
        }
    }

    private fun showSnackbar(
        snackStrId: Int,
        actionStrId: Int = 0,
        listener: View.OnClickListener? = null
    ) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), getString(snackStrId),
            BaseTransientBottomBar.LENGTH_INDEFINITE
        )
        if (actionStrId != 0 && listener != null) {
            snackbar.setAction(getString(actionStrId), listener)
        }
        snackbar.show()
    }


}