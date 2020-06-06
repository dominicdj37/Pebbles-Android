package com.pebbles.core

import android.app.Application
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.pebbles.Utils.ResourceUtils
import com.pebbles.core.Constants.APP_TAG

class AppContext: Application() {

    companion object{

    }

    override fun onCreate() {
        Log.d(APP_TAG,"Application started")
        super.onCreate()
        ResourceUtils.initialize(this)

        //application wide
    }

}