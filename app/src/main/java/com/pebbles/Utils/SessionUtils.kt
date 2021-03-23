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
        private const val USER_ALERTS_PREFERENCE = "USER_ALERTS_PREFERENCE"
        private const val BIOMETRIC_SHOWN_FLAG = "BIOMETRIC_SHOWN_FLAG"
        private const val BIOMETRIC_ENABLED_FLAG = "BIOMETRIC_ENABLED_FLAG"
        private const val DEVICE_FCM_TOKEN = "DEVICE_FCM_TOKEN"
        private const val COOKIES = "cookies"


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


    //region cookies
    fun updateCookies(newCookies: HashSet<String>) {
        val appCookies =  getCookies()  //retrieve stored cookies if any

        //removing all existing cookies with the same key
        newCookies.forEach { newCookie ->
            appCookies.filter { appCookie -> appCookie.substringBefore("=") == newCookie.substringBefore("=") }.let {
                appCookies.removeAll(it)
            }
        }

        //adding all new cookies
        appCookies.addAll(newCookies)
        sessionPrefs?.edit()?.putStringSet(COOKIES, appCookies)?.apply()
    }

    fun getCookies(): MutableSet<String> {
        return sessionPrefs?.getStringSet(COOKIES, HashSet()) ?: HashSet()
    }

    fun clearCookies() {
        sessionPrefs?.edit()?.remove(COOKIES)?.apply()
    }

    fun hasCookies(): Boolean {
        return getCookies().isNotEmpty()
    }
    //endregion


    fun setShouldRegenerateToken(shouldAsk: Boolean) {
        sessionPrefs?.edit()?.putBoolean(SHOULD_ASK_NOTIFICATION,shouldAsk)?.apply()
    }

    fun getShouldRegenerateToken(): Boolean {
        return sessionPrefs?.getBoolean(SHOULD_ASK_NOTIFICATION, true) ?: true
    }

    fun setUserAlertsOn(on: Boolean) {
        sessionPrefs?.edit()?.putBoolean(USER_ALERTS_PREFERENCE,on)?.apply()
    }

    fun getUserAlertsOn(): Boolean {
        return sessionPrefs?.getBoolean(USER_ALERTS_PREFERENCE, true) ?: true
    }


    fun setLastDeviceToken(token: String) {
        sessionPrefs?.edit()?.putString(DEVICE_FCM_TOKEN, token)?.apply()
    }

    fun getLastDeviceToken(): String? {
        return sessionPrefs?.getString(DEVICE_FCM_TOKEN, null)
    }




    fun setBiometricSetupShownFlag() {
        sessionPrefs?.edit()?.putBoolean(BIOMETRIC_SHOWN_FLAG,true)?.apply()
    }

    fun getBiometricSetupShownFlag(): Boolean {
        return sessionPrefs?.getBoolean(SHOULD_ASK_NOTIFICATION, false) ?: false
    }

    fun setBiometricEnabledFlag(set: Boolean) {
        sessionPrefs?.edit()?.putBoolean(BIOMETRIC_ENABLED_FLAG,set)?.apply()
    }

    fun getBiometricEnabledFlag(): Boolean {
        return sessionPrefs?.getBoolean(BIOMETRIC_ENABLED_FLAG, false) ?: false
    }




}