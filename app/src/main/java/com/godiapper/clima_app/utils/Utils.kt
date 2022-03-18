package com.godiapper.clima_app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

fun checkForInternet(context: Context):Boolean{
    //registrar la actividad con el servicio connectivity manager
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    //Si la version de android M o mayor se usa NetworkCapabilities del dispositivo
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        //Devuelve un objeto de tipo network correspondiente a la conectividad del dispositivo
        val network = connectivityManager.activeNetwork ?: return false
        //Representacion of the capabilities of an active network
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            //Indica si la red usa transporte wifi o tiene conectividad wifi
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            // Indica si la red tiene conectividad por datos moviles
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            else -> false
        }
    }else {
        // Si la version
        @Suppress("DEPRECATION") val networkInfo =
            connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION")
        return networkInfo.isConnected
    }
}