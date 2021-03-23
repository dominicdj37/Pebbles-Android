package com.pebbles.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MobileDevice {

    @SerializedName("id") @Expose
    var id: Int? = null

    @SerializedName("fcm_tocken") @Expose
    var token: String? = null

    @SerializedName("user_id") @Expose
    var userId: String? = null

}