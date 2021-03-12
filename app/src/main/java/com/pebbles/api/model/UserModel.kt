package com.pebbles.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserModel {

    @SerializedName("username") @Expose
    var username: String? = null

    @SerializedName("email") @Expose
    var email: String? = null

}