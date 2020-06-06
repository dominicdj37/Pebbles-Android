package com.pebbles.core

import com.firebase.ui.auth.IdpResponse
import com.pebbles.data.Device
import com.pebbles.data.User

object Repo {
    var firebaseLoginResponse: IdpResponse? = null
    var user: User? = null

    val devices: ArrayList<Device>
        get() = user?.devices ?: arrayListOf()

    val deviceShortCuts: ArrayList<Long> = arrayListOf()

}