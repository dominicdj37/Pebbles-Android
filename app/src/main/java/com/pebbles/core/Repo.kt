package com.pebbles.core

import com.firebase.ui.auth.IdpResponse
import com.pebbles.data.*
import java.util.HashMap

object Repo {


    var settings: EnvironmentSettings? = null
    var tokens: HashMap<String, String> = hashMapOf()
    var myToken: String? = null
    var firebaseLoginResponse: IdpResponse? = null
    var user: User? = null

    val devices: ArrayList<Device>
        get() = user?.devices ?: arrayListOf()

    val users: ArrayList<User> = arrayListOf()

    val deviceShortCuts: ArrayList<Shortcuts> = arrayListOf()
    var selectedShortCutAddPosition: String? = "s1"


    val months = arrayListOf<String>("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    var currentTempGraphData: java.util.ArrayList<Pair<String, Double>> = arrayListOf()
    var selectedDay = ""
    var selectedMonth = ""
    var selectedYear = ""


    //region chat
    val chats:ArrayList<ChatItem> = arrayListOf()
    //endregion

}