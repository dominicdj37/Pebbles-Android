package com.pebbles.ui.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import com.pebbles.R


open class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //For night mode theme

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }


    @Suppress("SameParameterValue")
    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    fun navigateToHome() {
        startActivity(Intent(this, HomePageActivity::class.java))
    }

    fun showDismissiveAlertDialog(title: String, message: String, onDismiss: () -> Unit? = {}) {
            val mAlertDialog = AlertDialog.Builder(this).create()
            mAlertDialog.setTitle(title)
            mAlertDialog.setMessage(message)
            mAlertDialog.setCancelable(false)
            mAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ok", Message())
            mAlertDialog.setOnShowListener {
                val positive = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positive?.transformationMethod = null

                positive?.setOnClickListener {
                    mAlertDialog.dismiss()
                    onDismiss.invoke()
                }
            }
            mAlertDialog.show()
    }

    fun navigateToSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    fun showEnableBioMetricDialog(onNo: () -> Unit? ,onYes: () -> Unit?) {
        val dialog = Dialog(this, R.style.FullScreenDialogTheme)
        dialog.setCancelable(false)
        dialog.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        dialog.setContentView(R.layout.layout_enable_biometric)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val takeMeThereTextView = dialog.findViewById(R.id.takeMeThereTextView) as TextView
        val cancelTextView = dialog.findViewById(R.id.cancelTextView) as TextView

        takeMeThereTextView.setOnClickListener {
            onYes.invoke()
            dialog.dismiss()
        }
        cancelTextView.setOnClickListener {
            onNo.invoke()
            dialog.dismiss()

        }

        dialog.show()

    }




}