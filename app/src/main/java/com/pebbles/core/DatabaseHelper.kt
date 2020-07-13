package com.pebbles.core

import android.util.Log
import com.google.firebase.database.*
import com.pebbles.Utils.NotificationUtils.TAG
import com.pebbles.core.Constants.APP_TAG
import com.pebbles.data.Device
import com.pebbles.data.EnvironmentSettings
import com.pebbles.data.Shortcuts
import com.pebbles.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import kotlin.collections.ArrayList


object DatabaseHelper {

    var databaseReference: DatabaseReference? = null

    fun initialize() {
        databaseReference = FirebaseDatabase.getInstance().reference
    }

    fun addUserForId(id: String, user: User?) {
        databaseReference?.child("users")?.child(id)?.setValue(user)
    }

    fun updateFCMToken(token: String?, onSucess: () -> Unit) {
        token?.let {
            databaseReference?.child("fcmTokens")?.child(it)?.setValue(token) { er, ref ->
                if (er == null) {
                    onSucess.invoke()
                }
            }
        }
    }


    fun getEnvironmentSettings(onFetched: (EnvironmentSettings?) -> Unit) {
        databaseReference?.child("settings")?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    onFetched.invoke(null)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        onFetched.invoke(p0.getValue(EnvironmentSettings::class.java))
                    } else {
                        onFetched.invoke(null)
                    }
                }

            })
    }

    fun returnUserForUid(uid: String, onFetched: (User?) -> Unit, onError: () -> Unit) {
        databaseReference?.child("users")?.child(uid)
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    onError.invoke()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        onFetched.invoke(p0.getValue(User::class.java))
                    } else {
                        onFetched.invoke(null)
                    }
                }

            })
    }

    fun returnDevicesForUid(uid: String, onFetched: (ArrayList<Device>?) -> Unit, onError: () -> Unit) {
        databaseReference?.child("devices")?.child(uid)
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    onError.invoke()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {

                        p0.children.forEach {dataSnap->
                            dataSnap.getValue(Device::class.java)?.let { device ->
                                Repo.user?.devices?.add(device)
                                Log.d(APP_TAG,device.toString())
                            }
                        }
                        onFetched.invoke(Repo.user?.devices)

                    } else {
                        onFetched.invoke(null)
                    }
                }

            })
    }

    fun returnUserShortCuts(onFetched: () -> Unit, onError: () -> Unit) {
        databaseReference?.child("deviceShortcuts")?.child(Repo.user?.deviceSetId!!)
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                onError.invoke()
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    Repo.deviceShortCuts.clear()
                    p0.children.forEach{ shortcuts->
                         val id = shortcuts.child(shortcuts.key.toString()).value
                            Repo.deviceShortCuts.add(Shortcuts(shortcuts.key, id as Long?) )
                        }
                    onFetched.invoke()
                } else {
                    Repo.deviceShortCuts.clear()
                    onFetched.invoke()
                }
            }

        })
    }

    fun addDeviceShortCut(device: Device, onFetched: () -> Unit, onError: () -> Unit) {
        Repo.selectedShortCutAddPosition?.let { it ->
            databaseReference?.child("deviceShortcuts")?.child(Repo.user?.deviceSetId!!)?.child(it)?.child(it)?.setValue(device.id) { er, ref ->
                if (er == null) {
                        when {
                            it[1] == '1' -> {
                                Repo.selectedShortCutAddPosition = "s2"
                            }
                            it[1] == '2' -> {
                                Repo.selectedShortCutAddPosition = "s3"
                            }
                            it[1] == '3' -> {
                                Repo.selectedShortCutAddPosition = "s4"
                            }
                            it[1] == '4' -> {
                                Repo.selectedShortCutAddPosition = "s1"
                            }
                    }

                    onFetched.invoke()
                } else {
                    onError.invoke()
                }
            }
        }

    }

    fun removeShortcut(key: String, device: Device, onFetched: () -> Unit) {
            databaseReference?.child("deviceShortcuts")?.child(Repo.user?.deviceSetId!!)?.child(key)?.removeValue { err, ref ->
                if(err == null) {
                        onFetched.invoke()
                }
            }
    }


    fun switchDevice(device: Device, onSwitched: () -> Unit, onError: () -> Unit) {
        device.port.let {
            val port = "D$it"
            val newState = if(device.state == 1) 0 else 1
            databaseReference?.child("portData")?.child(Repo.user?.deviceSetId!!)?.child(port)?.setValue(newState) { er, ref ->
                if (er == null) {
                    onSwitched.invoke()

                    sendSwitchedNotification(newState,it)

                } else {
                    onError.invoke()
                }
            }
        }
    }

    fun sendSwitchedNotification(newState: Int, deviceId: Int) {
        Repo.settings?.fcmServerKey?.let { serverKey ->
            // create your json here
            // create your json here

            val onOff = if (newState == 1) "on" else "off"
            val device =  if (deviceId == 1) "Filter" else "Light"

            Repo.tokens.forEach { tokenMap->
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        val jsonObject = JSONObject()
                        try {
                            val data = JSONObject()
                            data.put("title", "Hi there!")
                            data.put("description", "${Repo.user?.name} switched $onOff the $device")
                            data.put("notification_mode", "new")
                            jsonObject.put("to", tokenMap.value)
                            jsonObject.put("data", data)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        val client = OkHttpClient()
                        val json: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
                        // put your json here
                        // put your json here
                        val body = jsonObject.toString().toRequestBody(json)
                        val request: Request = Request.Builder()
                            .url("https://fcm.googleapis.com/fcm/send")
                            .addHeader("Authorization", "key=$serverKey")
                            .addHeader("Content-Type", "application/json")
                            .post(body)
                            .build()

                        var response: Response? = null
                        try {
                            response = client.newCall(request).execute()
                            val resStr: String = response.body.toString()
                            Log.d(TAG, "send notification success: $resStr")
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Log.d(TAG, "send notification failed: ${e.message}")
                        }
                    }
                }
            }
        }
    }


    fun returnTempDataFor(day:String, month:String, year:String, onFetched: (ArrayList<Pair<String,Double>>) -> Unit) {
        val ref = databaseReference?.child("sensorData")?.child(Repo.user?.deviceSetId!!)?.child("tempValue")?.child(year)?.child(month)?.child(day)
        ref?.orderByKey()
        ref?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    onFetched.invoke(arrayListOf())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        val list = arrayListOf<Pair<String,Double>>()
                        p0.children.forEach{ tempData ->
                            try {
                                tempData.key?.let { key->
                                    (tempData.value as Double).let { value->
                                        list.add(Pair(key, value))
                                    }
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        onFetched.invoke(list)
                    } else {
                        onFetched.invoke(arrayListOf())
                    }
                }

            })
    }


}