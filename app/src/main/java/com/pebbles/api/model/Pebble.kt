package com.pebbles.api.model

import com.firebase.ui.auth.data.model.User
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.pebbles.api.repository.SessionRepository
import com.pebbles.core.sessionUtils

class Pebble {



    @SerializedName("id") @Expose
    var id: Int? = null

    @SerializedName("pebble_name") @Expose
    var pebbleName: String? = null

    @SerializedName("owner_user_id") @Expose
    var ownerId:Int? = null

    @SerializedName("device_count") @Expose
    var deviceCount: Int? = null

    @SerializedName("up_time") @Expose
    var upTime: String? = null

    @SerializedName("status") @Expose
    var status: String? = null

    @SerializedName("users") @Expose
    var users: ArrayList<UserModel> = arrayListOf()

    val owner: UserModel?
        get() = users.find { it.id == ownerId }

    fun isMine(): Boolean {
        return  SessionRepository.getInstance().user?.id == ownerId
    }
}