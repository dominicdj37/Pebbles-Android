package com.pebbles.core

import com.firebase.ui.auth.IdpResponse
import com.pebbles.data.Device
import com.pebbles.data.EnvironmentSettings
import com.pebbles.data.Shortcuts
import com.pebbles.data.User
import java.util.HashMap

object Repo {


    var settings: EnvironmentSettings? = null
    var tokens: HashMap<String, String> = hashMapOf()
    var myToken: String? = null
    var firebaseLoginResponse: IdpResponse? = null
    var user: User? = null

    val devices: ArrayList<Device>
        get() = user?.devices ?: arrayListOf()

    val deviceShortCuts: ArrayList<Shortcuts> = arrayListOf()
    var selectedShortCutAddPosition: String? = "s1"

}