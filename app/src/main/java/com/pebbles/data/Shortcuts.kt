package com.pebbles.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Shortcuts(
    val tag:String? = null,
    val deviceID: Long?= null
)