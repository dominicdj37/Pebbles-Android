package com.pebbles.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


object HttpStatusCode {
    const val successOne = 200
    const val successTwo = 201
    const val successThree = 204

    const val badRequest = 400
    const val unauthorized = 401
    const val forbidden = 403
    const val notFound = 404
    const val unProcessableEntity = 422

    const val internalServerError = 500
    const val timeout = 504

    const val noInternet = 15001
    const val customErrorCode = 15002
    const val passwordErrorCode = 15003
    const val quizExpired = 15004
    const val illegalArgumentException = 15005
}



class ErrorBody {

    @SerializedName("error") @Expose
    val error: Error? = null
}


class Error {

    @SerializedName("code") @Expose
    var mCode: Int? = null

    @SerializedName("message") @Expose
    var message: String? = null

    @SerializedName("error_code_params") @Expose
    var params: ErrorCodeParams? = null
}

class ErrorCodeParams {
    @SerializedName("error_type") @Expose
    var errorType: String? = null
}