package com.pebbles.Utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.pebbles.core.sessionUtils
import com.pebbles.data.PushNotification

object NotificationUtils {

    const val TAG = "MyFirebaseMsgService"
    val updateTokenLiveData: MutableLiveData<String> = MutableLiveData()

    fun initFireBase( onTokenGenerated: ((String)-> Unit)? = null, onTokenGenerationFailed: (()-> Unit)? = null) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d(TAG, "getInstanceId failed", task.exception)
                    onTokenGenerationFailed?.invoke()
                }
                val token = task.result?.token
                token?.let { onTokenGenerated?.invoke(it) }
                Log.d(TAG,"token: $token")
            })
    }

    fun shouldRegenerateToken(): Boolean {
        return sessionUtils.getShouldRegenerateToken()
    }

    fun setTokenRegistered() {
        sessionUtils.setShouldRegenerateToken(false)
    }

    fun setTokenDestroyed() {
        sessionUtils.setShouldRegenerateToken(true)
    }


    fun getPushNotificationFromData(data: MutableMap<String, String>): PushNotification {
        val notificationObject = PushNotification()
        notificationObject.parseData(data)
        return  notificationObject
    }


}