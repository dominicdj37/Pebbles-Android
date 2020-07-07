package com.pebbles.core

import android.util.Log
import com.google.firebase.database.*
import com.pebbles.core.Constants.APP_TAG
import com.pebbles.core.Repo.user
import com.pebbles.data.Device
import com.pebbles.data.EnvironmentSettings
import com.pebbles.data.Shortcuts
import com.pebbles.data.User

object DatabaseHelper {

    var databaseReference: DatabaseReference? = null

    fun initialize() {
        databaseReference = FirebaseDatabase.getInstance().reference
    }

    fun addUserForId(id: String, user: User?) {
        databaseReference?.child("users")?.child(id)?.setValue(user)
    }

    fun updateFCMToken(token: String?, onSucess: () -> Unit) {
        databaseReference?.child("fcmTokens")?.child(user?.id!!)?.setValue(token) { er, ref ->
            if (er == null) {
                onSucess.invoke()
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
                } else {
                    onError.invoke()
                }
            }
        }
    }

}