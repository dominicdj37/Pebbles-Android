package com.pebbles.core

import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseUser

object Repo {
    var response: IdpResponse? = null
    var user: FirebaseUser? = null

}