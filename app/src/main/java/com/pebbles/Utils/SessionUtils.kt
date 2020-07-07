package com.pebbles.Utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class SessionUtils(val context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var sessionPrefs = context.getSharedPreferences(PEBBLES_PREFERENCE, Context.MODE_PRIVATE)


    companion object {
        //region SharedPreference Key
        private const val PEBBLES_PREFERENCE = "Pebbles_Preference"
        private const val SHOULD_ASK_NOTIFICATION = "SHOULD_ASK_NOTIFICATION"


        //endregion
    }


    //region network connection
    fun isNetworkConnected(): Boolean {
        val networkInfo = connectivityManager.activeNetwork
        if (networkInfo != null) {
            val nc = connectivityManager.getNetworkCapabilities(networkInfo)
            return nc != null && (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI))
        }
        return false
    }


    fun setShouldRegenerateToken(shouldAsk: Boolean) {
        sessionPrefs?.edit()?.putBoolean(SHOULD_ASK_NOTIFICATION,shouldAsk)?.apply()
    }

    fun getShouldRegenerateToken(): Boolean {
        return sessionPrefs?.getBoolean(SHOULD_ASK_NOTIFICATION, true) ?: true
    }
}