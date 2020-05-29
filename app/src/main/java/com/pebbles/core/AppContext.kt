package com.pebbles.core

import android.app.Application
import android.util.Log
import com.pebbles.core.Constants.APP_TAG

class AppContext: Application() {

    companion object{

    }

    override fun onCreate() {
        Log.d(APP_TAG,"Application started")
        super.onCreate()
        //application wide
    }

}