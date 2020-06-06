package com.pebbles.core

import android.util.Log
import com.google.firebase.database.*
import com.pebbles.core.Constants.APP_TAG
import com.pebbles.data.Device
import com.pebbles.data.User

object DatabaseHelper {

    private var databaseReference: DatabaseReference? = null

    fun initialize() {
        databaseReference = FirebaseDatabase.getInstance().reference
    }

    fun addUserForId(id: String, user: User?) {
        databaseReference?.child("users")?.child(id)?.setValue(user)
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
        databaseReference?.child("deviceShortcuts")?.child(Repo.user?.id!!)
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                onError.invoke()
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    p0.children.forEach { deviceId->
                        deviceId.value?.let { deviceId ->
                            Repo.deviceShortCuts.add(deviceId as Long)
                        }
                    }
                    onFetched.invoke()

                } else {

                }
            }

        })
    }

}