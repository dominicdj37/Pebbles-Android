package com.pebbles.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Pebbles {

    @SerializedName("my_pebbles") @Expose
    var my_pebbles: ArrayList<Pebble> = arrayListOf()

}