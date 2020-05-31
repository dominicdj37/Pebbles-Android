package com.pebbles.core

import com.google.firebase.database.*
import com.pebbles.data.User

object DatabaseHelper {

    private var databaseReference: DatabaseReference? = null

    fun initialize() {
        databaseReference = FirebaseDatabase.getInstance().reference
    }

    fun addUserForId(id:String, user: User?) {
        databaseReference?.child("users")?.child(id)?.setValue(user)
    }

    fun returnUserForUid(uid: String, onFetched: (User?) -> Unit, onError: () -> Unit) {
        databaseReference?.child("users")?.child(uid)?.addListenerForSingleValueEvent(object: ValueEventListener {
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

}