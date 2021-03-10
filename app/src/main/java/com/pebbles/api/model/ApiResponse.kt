package com.pebbles.api.model

import io.reactivex.internal.util.ErrorMode

class ApiResponse() {
    var data: Any? = null
    var error: Error? = null

    constructor(data: Any) : this() {
        this.data = data
    }

    constructor(t: Throwable): this() {
        this.error = Error().apply {
            message = t.message
        }
    }

}