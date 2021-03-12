package com.pebbles.api.model

import io.reactivex.internal.util.ErrorMode

class ApiResponse() {
    var sucess: Boolean? = false
    var error: Error? = null

    constructor(success: Boolean) : this() {
        this.sucess = success
    }

    constructor(t: Throwable): this() {
        this.error = Error().apply {
            message = t.message
        }
    }

}