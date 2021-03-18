package com.pebbles.api.core

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.pebbles.api.model.ResponseModel
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET("settings.json")
    fun getSettings(): Observable<ResponseModel>


    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @POST
    fun autoLogin(@Url url: String): Observable<ResponseModel>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @POST
    fun login(@Url url: String): Observable<ResponseModel>



    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @POST
    fun signUp(@Url url: String): Observable<ResponseModel>


    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET()
    fun getpebbles(@Url url: String): Observable<ResponseModel>

}