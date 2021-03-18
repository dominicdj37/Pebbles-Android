package com.pebbles.api.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pebbles.api.core.APIGlobal
import com.pebbles.api.core.ApiConstants
import com.pebbles.api.core.ApiConstants.PEBBELS
import com.pebbles.api.core.ApiConstants.formatUrl
import com.pebbles.api.model.ApiResponse
import com.pebbles.api.model.Pebbles
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PebblesRepository private constructor() {

    companion object {
        private var mInstance: PebblesRepository? = null
        fun getInstance(): PebblesRepository {
            if (mInstance == null) {
                synchronized(this) {
                    mInstance = PebblesRepository()
                }
            }
            return mInstance!!
        }
    }

    var pebbles:Pebbles? = null

    @SuppressLint("CheckResult")
    fun getPebbles(username: String, pebblesLiveData: MutableLiveData<ApiResponse>) {
        val url = ApiConstants.getRequestUrlFor(PEBBELS).formatUrl(username)
        APIGlobal.create().getpebbles(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ responseModel ->
                    val gson = Gson()
                    pebbles = gson.fromJson(gson.toJson(responseModel.result), Pebbles::class.java)
                    pebblesLiveData.postValue(ApiResponse(true))
                }, { error ->
                    pebblesLiveData.postValue(ApiResponse((error)))
                })
    }



}