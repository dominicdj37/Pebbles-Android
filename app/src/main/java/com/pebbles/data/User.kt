package com.pebbles.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var name:String? = null,
    var email:String? = null,
    var profilePhotoUrl: String? = null,
    var deviceSetId:String = "bO194hKhXrcJiDJJb2AuTGygHzq1",
    var id:String? = null,
    var devices: ArrayList<Device> = arrayListOf()
)