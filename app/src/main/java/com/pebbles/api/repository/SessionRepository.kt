package com.pebbles.api.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pebbles.api.core.APIGlobal
import com.pebbles.api.core.ApiConstants
import com.pebbles.api.core.ApiConstants.LOGIN
import com.pebbles.api.core.ApiConstants.formatUrl
import com.pebbles.api.model.ApiResponse
import com.pebbles.api.model.Settings
import com.pebbles.api.model.UserModel
import com.pebbles.data.User
import com.pebbles.interfaces.NetworkResponseCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SessionRepository private constructor() {

    companion object {
        private var mInstance: SessionRepository? = null
        fun getInstance(): SessionRepository {
            if (mInstance == null) {
                synchronized(this) {
                    mInstance = SessionRepository()
                }
            }
            return mInstance!!
        }
    }

    var user:UserModel? = null

    @SuppressLint("CheckResult")
    fun login(username:String, password:String): MutableLiveData<ApiResponse> {
        val responseData = MutableLiveData<ApiResponse>()
        val url = ApiConstants.getRequestUrlFor(LOGIN).formatUrl(username, password)
        APIGlobal.create().login(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ responseModel ->
                    val gson = Gson()
                    user = gson.fromJson(gson.toJson(responseModel.result), UserModel::class.java)
                    responseData.postValue(ApiResponse(true))
                }, { error ->
                    responseData.postValue(ApiResponse((error)))
                })
        return responseData
    }
    }
}