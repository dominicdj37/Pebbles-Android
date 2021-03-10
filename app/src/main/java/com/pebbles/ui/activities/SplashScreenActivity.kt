package com.pebbles.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pebbles.R
import com.pebbles.Utils.BiometricUtils
import com.pebbles.api.core.APIGlobal
import com.pebbles.api.repository.SettingRepository
import com.pebbles.core.Constants.APP_TAG
import com.pebbles.core.Constants.RC_SIGN_IN
import com.pebbles.core.DatabaseHelper
import com.pebbles.core.Repo
import com.pebbles.core.Run
import com.pebbles.core.sessionUtils
import com.pebbles.data.User
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        startSplashTimer()
        progress_signIn.visibility = View.VISIBLE
        loginButton.visibility = View.GONE
        fingerPrintIcon.visibility = View.GONE

        getSettings()
    }

    private fun getSettings() {
        SettingRepository.getEnvironmentSetting()
    }

    private fun startSplashTimer() {
        Run.after(2000) {
            if(FirebaseAuth.getInstance().currentUser != null) {
                onLoginSuccess(null, FirebaseAuth.getInstance().currentUser)
            } else {

                loginButton.visibility = View.VISIBLE
                progress_signIn.visibility = View.GONE
                loginButton.setOnClickListener {
                    loginButton.visibility = View.GONE
                    progress_signIn.visibility = View.VISIBLE
                    navigateToFirebaseLogin()
                }
            }
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
                .setIsSmartLockEnabled(false)
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

                loginButton.visibility = View.VISIBLE
                progress_signIn.visibility = View.GONE
            }
        }
    }

    private fun onLoginSuccess(response: IdpResponse?, user: FirebaseUser?) {
        Log.d(APP_TAG, "sign in response: $response")
        Log.d(APP_TAG, "sign in user: $user")
        Repo.firebaseLoginResponse = response

        DatabaseHelper.initialize()

        getEnvironmentSettings {
            checkUserExistAndUpdate(response, user) {
                initiateBiometricLogin()
            }
        }
    }

    private fun initiateBiometricLogin() {
        checkBiometricAuthentication {
            navigateToHome()
            finish()
        }
    }


    private fun checkBiometricAuthentication(function: () -> Unit) =
        if(sessionUtils.getBiometricEnabledFlag()) {
            BiometricUtils.afterBiometricAuth(onAuth = {
                Log.d(BiometricUtils.TAG, "Login after biometric auth")
                function.invoke()
            },onCancel = {
                //show sensor again
                fingerPrintIcon.visibility = View.VISIBLE
                progress_signIn.visibility =  View.GONE
                fingerPrintIcon.setOnClickListener {
                    fingerPrintIcon.visibility = View.GONE
                    progress_signIn.visibility =  View.VISIBLE
                    initiateBiometricLogin()
                }

            }, onFailed = {
                //todo show pin entry
                function.invoke()
            }, context = this )

        } else {
            function.invoke()
        }


    private fun getEnvironmentSettings(onFetch: () -> Unit) {
        DatabaseHelper.getEnvironmentSettings {
            Repo.settings = it
            onFetch.invoke()
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