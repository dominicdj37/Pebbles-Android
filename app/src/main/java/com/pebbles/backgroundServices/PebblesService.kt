package com.pebbles.backgroundServices

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pebbles.R
import com.pebbles.core.DatabaseHelper
import com.pebbles.core.Repo
import com.pebbles.ui.activities.SplashScreenActivity
import com.pebbles.ui.fragments.DeviceFragment
import java.security.Provider

class PebblesService: Service() {


    private val CHANNEL_ID = "PebblesService"


    companion object {
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, PebblesService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, PebblesService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, SplashScreenActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        val notificationLayout = RemoteViews(packageName, R.layout.notificaton_layout)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setColor(getColor(R.color.colorPrimary2))
            .setColorized(true)
            .setCustomContentView(notificationLayout)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(200, notification)

        initDeviceStateListener()

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Pebbles Service",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            val uri: Uri = Uri.parse("android.resource://" + this.packageName + "/" + R.raw.drop)
            val att = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            serviceChannel.setSound(uri, att)
            manager?.createNotificationChannel(serviceChannel)
        }
    }


    private fun initDeviceStateListener() {
        val messageListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val portData = dataSnapshot.value as HashMap<String,Long>
                    portData.forEach { (port, state) ->
                        Repo.devices.find { device -> device.port.toString() == port[1].toString()}?.state = state.toInt()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read value
            }
        }

        Repo.user?.deviceSetId?.let { DatabaseHelper.databaseReference?.child("portData")?.child(it)?.addValueEventListener(messageListener) }

    }

}