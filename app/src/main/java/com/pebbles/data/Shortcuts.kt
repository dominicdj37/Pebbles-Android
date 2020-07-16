package com.pebbles.data

import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Keep
data class Shortcuts(
    val tag:String? = null,
    val deviceID: Long?= null
)