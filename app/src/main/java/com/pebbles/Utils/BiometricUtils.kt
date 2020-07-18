package com.pebbles.Utils

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference
import java.util.concurrent.Executor

object BiometricUtils {



    private var contextRef: WeakReference<Context>? = null
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    const val TAG = "BIOMETRIC_TAG"


    //region initialize
    fun initialize(context: Context) {
        contextRef = WeakReference(context)
    }
    //endregion

    fun afterBiometricAuth(onAuth: () -> Unit, onCancel: () -> Unit, onFailed: () -> Unit, context: FragmentActivity) {
        if(checkBiometricsAvailable(context)) {
            showBioAuthDialog(onAuth, onFailed, onCancel, context)
        } else {
            onFailed.invoke()
        }
    }

    fun checkBiometricsAvailable(context: FragmentActivity): Boolean{
        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d(TAG, "App can authenticate using biometrics.")
                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.d(TAG, "No biometric features available on this device.")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.d(TAG, "Biometric features are currently unavailable.")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.d(TAG, "The user hasn't associated " + "any biometric credentials with their account.")
                return false
            }
            else -> return false
        }
    }


    private fun showBioAuthDialog(onAuth: () -> Unit, onFailed: () -> Unit, onCanceled: () -> Unit, context: FragmentActivity) {
        executor = ContextCompat.getMainExecutor(context)
        biometricPrompt = BiometricPrompt(context, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Log.d(TAG, "Canceled")
                    onCanceled.invoke()
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onAuth.invoke()
                    Log.d(TAG, "Success")
                    super.onAuthenticationSucceeded(result)
                }

                override fun onAuthenticationFailed() {
                    Log.d(TAG, "Denied")
                    super.onAuthenticationFailed()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setDeviceCredentialAllowed(true)
            .setSubtitle("Log in using your biometric credential")
            .build()

        biometricPrompt.authenticate(promptInfo)

    }

}