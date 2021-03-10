package com.pebbles.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Settings {

    @SerializedName("app_name") @Expose
    var app_name: String? = null

    @SerializedName("version") @Expose
    var version: String? = null

    @SerializedName("firebase_api_key") @Expose
    var firebase_api_key: String? = null
}