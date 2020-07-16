package com.pebbles.data

class PushNotification {
        var title: String? = null
        var description: String? = null

        var notificationId: Int? = null
        var notificationMode: String? = null

        fun parseData(data: MutableMap<String, String>) {
            title = data["title"]
            description = data["description"]
            notificationMode = data["notification_mode"]
            notificationId = data["notification_id"]?.toInt()
        }
}