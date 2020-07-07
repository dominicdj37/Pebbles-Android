package com.pebbles.backgroundServices

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pebbles.R
import com.pebbles.Utils.NotificationUtils
import com.pebbles.core.Constants.NOTIFICATION_CHANNEL
import com.pebbles.core.Constants.NOTIFICATION_CHANNEL_DESCRIPTION
import com.pebbles.core.Constants.PushNotificationMode.DELETE
import com.pebbles.core.Constants.PushNotificationMode.NEW
import com.pebbles.core.Constants.PushNotificationMode.UPDATE
import com.pebbles.core.Repo
import com.pebbles.data.PushNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PushNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // todo Handle messages here.

        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "Notification data payload: ${remoteMessage.data}")
        remoteMessage.data.isNotEmpty().let {
            handleNow(remoteMessage.data) //this should be done within 10 seconds or else use scheduleJob
        }
    }

    private fun handleNow(data: MutableMap<String, String>) {
        NotificationUtils.getPushNotificationFromData(data).let {

            it.notificationId = SystemClock.uptimeMillis().toInt()

            when (it.notificationMode) {
                NEW -> postNewNotification(it)
                DELETE -> deleteNotification(it)
                UPDATE -> postNewNotification(it)
                null -> {
                }
            }
        }
    }


    private fun postNewNotification(data: PushNotification) {


        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(data.title)
            .setSubText(data.subtitle)
            .setColor(getColor(R.color.colorPrimaryDark))
            .setColorized(true)
            .setContentText(data.description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(data.description))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setGroup(data.notificationGroupId.toString())

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                NOTIFICATION_CHANNEL_DESCRIPTION,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }



        data.notificationId?.let {
            notificationManager.notify(it, notificationBuilder.build())

                val shouldCreateSummary = notificationManager.activeNotifications.filter { item-> item.id == data.notificationGroupId }.isNullOrEmpty() && data.notificationGroupId != null
                val hasMoreThanOneForSameGroup = notificationManager.activeNotifications.filter { item-> item.notification.group == data.notificationGroupId.toString() ?: "-1" }.size > 1
                if(hasMoreThanOneForSameGroup && shouldCreateSummary) {

                    val summaryNotificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setColor(getColor(R.color.colorPrimaryDark))
                        .setColorized(true)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(data.description))
                        .setAutoCancel(true)
                        .setGroup(data.notificationGroupId.toString())
                        .setGroupSummary(true)
                    data.notificationGroupId?.let { groupId -> notificationManager.notify(groupId, summaryNotificationBuilder.build()) }
                    Log.d(NotificationUtils.TAG, "Created summary for: ${data.notificationGroupId}")
                }
        } ?: let { Log.d(TAG, "POST: Notification Id null") }


    }

//    private fun getPendingIntentFromData(data: PushNotification): PendingIntent {
//
//        val intent = Intent(this, AppLinkingActivity::class.java)
//
//        intent.putExtra(NOTIFICATION_IS_FROM_NOTIFICATION, true)
//        intent.putExtra(NOTIFICATION_LINK_TYPE, data.notificationType)
//        intent.putExtra(NOTIFICATION_DOMAIN_NAME, data.domainName)
//        intent.putExtra(NOTIFICATION_COURSE_ID,  data.courseID)
//        intent.putExtra(NOTIFICATION_LECTURE_ID, data.lectureID)
//        intent.putExtra(NOTIFICATION_COMPONENT_ID, data.componentID)
//        intent.putExtra(NOTIFICATION_TEAM_SET_ID, data.teamSetID)
//        intent.putExtra(NOTIFICATION_ANNOUNCEMENT_ID, data.announcementID)
//
//        return PendingIntent.getActivity(this, data.notificationId!!, intent, PendingIntent.FLAG_CANCEL_CURRENT)
//    }

    private fun deleteNotification(data: PushNotification) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        data.notificationId?.let { notificationManager.cancel(it) } ?: let { Log.d(TAG, "DELETE: Notification Id null ") }
    }
    //endregion

    //region token
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        if (Repo.user != null) {
            sendRegistrationToServer(token)
        }
    }

    private fun sendRegistrationToServer(token: String?) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                NotificationUtils.updateTokenLiveData.postValue(token)
            }
        }
    }
    //endregion

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

}