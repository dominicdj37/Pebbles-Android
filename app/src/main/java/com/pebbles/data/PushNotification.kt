package com.pebbles.data

class PushNotification {
        var title: String? = null
        var subtitle: String? = null
        var description: String? = null

        var notificationId: Int? = null
        var notificationMode: String? = null
        var notificationGroupId: Int? = null

        var notificationType: String? = null
        var domainName: String? = null
        var courseID: String? = null
        var lectureID: String? = null
        var componentID: String? = null
        var teamSetID: String? = null
        var announcementID: String? = null

        fun parseData(data: MutableMap<String, String>) {
            title = data["title"]
            subtitle = data["subtitle"]
            description = data["description"]
            notificationMode = data["notification_mode"]
            notificationGroupId = data["grouping_id"]?.toIntOrNull()

            notificationType = data["notification_type"]
            domainName = data["domain_name"]
            courseID = data["course_id"]
            lectureID = data["lecture_page_id"]
            componentID = data["lecture_component_id"]
            teamSetID = data["team_set_id"]
            announcementID = data["announcement_id"]
        }
}