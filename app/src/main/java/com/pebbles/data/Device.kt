package com.pebbles.data

data class Device (
    var deviceId: Int? = null,
    var deviceName: String? = null,
    var isAutomated: Boolean = false,
    var isDeviceOn: Boolean = false,
    var isDeviceConnected: Boolean = false,
    var deviceImageUrl: String? = null
)