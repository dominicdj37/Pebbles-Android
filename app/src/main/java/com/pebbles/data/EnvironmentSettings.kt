package com.pebbles.data

import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Keep
data class EnvironmentSettings(
    var fcmServerKey:String? = null
)