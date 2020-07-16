package com.pebbles.data

import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Keep
data class User(
    var name:String? = null,
    var email:String? = null,
    var profilePhotoUrl: String? = null,
    var deviceSetId:String = "bO194hKhXrcJiDJJb2AuTGygHzq1",
    var id:String? = null,
    var devices: ArrayList<Device> = arrayListOf()
)