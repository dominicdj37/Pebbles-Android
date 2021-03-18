package com.pebbles.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Pebble {

    @SerializedName("pebble_name") @Expose
    var pebbleName: String? = null

    @SerializedName("users") @Expose
    var users: ArrayList<UserModel> = arrayListOf()

}