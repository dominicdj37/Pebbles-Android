package com.pebbles.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class EnvironmentSettings(
    var fcmServerKey:String? = null
)