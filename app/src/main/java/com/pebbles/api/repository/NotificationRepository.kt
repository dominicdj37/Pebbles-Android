package com.pebbles.api.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pebbles.api.core.APIGlobal
import com.pebbles.api.core.ApiConstants
import com.pebbles.api.core.ApiConstants.CREATE_PEBBLE
import com.pebbles.api.core.ApiConstants.PEBBLES
import com.pebbles.api.core.ApiConstants.SHARE_PEBBLE
import com.pebbles.api.core.ApiConstants.TOKEN_DE_REG
import com.pebbles.api.core.ApiConstants.TOKEN_REG
import com.pebbles.api.core.ApiConstants.formatUrl
import com.pebbles.api.model.ApiResponse
import com.pebbles.api.model.Pebbles
import com.pebbles.api.model.UserModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NotificationRepository private constructor() {

    companion object {
        private var mInstance: NotificationRepository? = null
        fun getInstance(): NotificationRepository {
            if (mInstance == null) {
                synchronized(this) {
                    mInstance = NotificationRepository()
                }
            }
            return mInstance!!
        }
    }

    @SuppressLint("CheckResult")
    fun registerToken(token: String): MutableLiveData<ApiResponse> {
        val userLiveData = MutableLiveData<ApiResponse>()
        val url = ApiConstants.getRequestUrlFor(TOKEN_REG).formatUrl(token)
        APIGlobal.create().registerToken(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ responseModel ->
                    val gson = Gson()
                    val user  = gson.fromJson(gson.toJson(responseModel.result), UserModel::class.java)
                    SessionRepository.getInstance().user = user
                    userLiveData.postValue(ApiResponse(true))
                }, { error ->
                    userLiveData.postValue(ApiResponse((error)))
                })
        return userLiveData
    }

    @SuppressLint("CheckResult")
    fun deRegisterToken(token: String): MutableLiveData<ApiResponse> {
        val userLiveData = MutableLiveData<ApiResponse>()
        val url = ApiConstants.getRequestUrlFor(TOKEN_DE_REG).formatUrl(token)
        APIGlobal.create().deRegisterToken(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ responseModel ->
                val gson = Gson()
                val user  = gson.fromJson(gson.toJson(responseModel.result), UserModel::class.java)
                SessionRepository.getInstance().user = user
                userLiveData.postValue(ApiResponse(true))
            }, { error ->
                userLiveData.postValue(ApiResponse((error)))
            })
        return userLiveData
    }

}