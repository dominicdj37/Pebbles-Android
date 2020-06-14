package com.pebbles.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Device(
    var id: Int? = null,
    var name: String? = null,
    var isAuto: Boolean = false,
    var isConnected: Boolean = false,
    var state: Int = -1,
    var imageUrl: String? = null,
    var port: Int = -1,
    var type: String? = null

)