package com.pebbles.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ErrorBody {

    @SerializedName("error") @Expose
    val error: Error? = null
}


class Error {

    @SerializedName("code") @Expose
    var mCode: String? = null

    @SerializedName("message") @Expose
    var message: String? = null
}