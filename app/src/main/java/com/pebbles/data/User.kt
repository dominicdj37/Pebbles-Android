package com.pebbles.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var name:String? = null,
    var email:String? = null,
    var profilePhotoUrl: String? = null,
    var id:String? = null
)