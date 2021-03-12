package com.pebbles.api.repository

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.pebbles.api.core.APIGlobal
import com.pebbles.api.model.ApiResponse
import com.pebbles.api.model.Settings
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SettingRepository {

    companion object {
        @SuppressLint("CheckResult")
        fun getEnvironmentSetting(): MutableLiveData<ApiResponse> {
            val responseData = MutableLiveData<ApiResponse>()
            try {
                APIGlobal.create().getSettings()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ responseModel ->
                        val gson = Gson()
                        val environmentSetting =
                            gson.fromJson(gson.toJson(responseModel.result), Settings::class.java)
                        responseData.postValue(ApiResponse(true))
                    }, { error ->
                        responseData.postValue(ApiResponse((error)))
                    })
            } catch (e: Exception) {
                responseData.postValue(ApiResponse((e)))
            }
            return responseData
        }
    }
}