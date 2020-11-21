package com.pebbles.data

import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Keep
data class ChatItem(
    var id: Int? = null,
    var user: User? = null
)