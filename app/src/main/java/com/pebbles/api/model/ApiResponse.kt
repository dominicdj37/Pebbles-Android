package com.pebbles.api.model

import io.reactivex.internal.util.ErrorMode
import retrofit2.HttpException

class ApiResponse() {
    var sucess: Boolean? = false
    var error: Error? = null

    constructor(success: Boolean) : this() {
        this.sucess = success
    }

    constructor(t: Throwable): this() {
        this.error = Error().apply {
            mCode = (t as HttpException).code()
            message = t.message
        }
    }

}