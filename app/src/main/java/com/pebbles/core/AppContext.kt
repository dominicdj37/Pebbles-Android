package com.pebbles.core

import android.app.Application
import android.util.Log
import com.pebbles.Utils.BiometricUtils
import com.pebbles.Utils.ResourceUtils
import com.pebbles.Utils.SessionUtils
import com.pebbles.core.Constants.APP_TAG


val sessionUtils: SessionUtils by lazy {
    AppContext.sessionUtils!!
}


class AppContext: Application() {

    companion object {
        var sessionUtils: SessionUtils? = null
    }
    override fun onCreate() {
        Log.d(APP_TAG,"Application started")
        super.onCreate()
        BiometricUtils.initialize(this)
        ResourceUtils.initialize(this)
        sessionUtils = SessionUtils(applicationContext)

        //application wide
    }

}