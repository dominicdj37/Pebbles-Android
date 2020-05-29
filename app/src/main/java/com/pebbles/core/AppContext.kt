package com.pebbles.core

import android.app.Application
import android.util.Log

class AppContext: Application() {

    companion object{

    }

    override fun onCreate() {
        super.onCreate()
        Log.d("App","onCreate")
        //application wide
    }

}