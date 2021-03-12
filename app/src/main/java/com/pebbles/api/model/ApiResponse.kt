package com.pebbles.api.model

import com.google.gson.Gson
import io.reactivex.internal.util.ErrorMode
import retrofit2.HttpException
import java.lang.Exception
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

class ApiResponse() {
    var sucess: Boolean? = false
    var error: Error? = null

    constructor(success: Boolean) : this() {
        this.sucess = success
    }

    constructor(throwable: Throwable): this() {

        this.error = Error()

        this.error?.message = throwable.message
        when (throwable) {
            is HttpException -> {
                this.error?.mCode = throwable.code()
                try {
                    val errorBody: ErrorBody? = Gson().fromJson(throwable.response()?.errorBody()?.string(), ErrorBody::class.java)
                    this.error = errorBody?.error
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            is UnknownHostException -> {
                this.error?.mCode = HttpStatusCode.notFound
            }

            is SocketTimeoutException -> {
                this.error?.mCode = HttpStatusCode.timeout
            }

            is ConnectException -> {
                this.error?.mCode = HttpStatusCode.noInternet
            }
        }
    }

}