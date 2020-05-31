package com.pebbles.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pebbles.R
import com.pebbles.core.Constants.APP_TAG
import com.pebbles.core.Constants.RC_SIGN_IN
import com.pebbles.core.DatabaseHelper
import com.pebbles.core.Repo
import com.pebbles.core.Run
import com.pebbles.data.User

class SplashScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        startSplashTimer()

    }

    private fun startSplashTimer() {
        Run.after(5000) {
            navigateToFirebaseLogin()
        }
    }

    //region firebase auth
    private fun navigateToFirebaseLogin() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.loginTheme)
                .setLogo(R.drawable.ic_logo)
                .build(), RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                onLoginSuccess(response, user)
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    private fun onLoginSuccess(response: IdpResponse?, user: FirebaseUser?) {
        Log.d(APP_TAG, "sign in response: $response")
        Log.d(APP_TAG, "sign in user: $user")
        Repo.firebaseLoginResponse = response

        DatabaseHelper.initialize()

        checkUserExistAndUpdate(response, user) {
            navigateToHome()
            finish()
        }
    }

    private fun checkUserExistAndUpdate(response: IdpResponse?, user: FirebaseUser?, onUpdate: () -> Unit) {
        DatabaseHelper.returnUserForUid(user?.uid.toString() ?: "null", onFetched = {
            if(it != null) {
                Repo.user = it
            } else {
                //add to database
                Repo.user = User().apply {
                    id = user?.uid.toString()
                    name = user?.displayName
                    email = user?.email
                    profilePhotoUrl = user?.photoUrl.toString()
                }
                DatabaseHelper.addUserForId(user?.uid.toString(), Repo.user)
            }
            onUpdate.invoke()
        }, onError = {
            //add log
        } )
    }


    //endregion

}