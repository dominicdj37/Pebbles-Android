package com.pebbles.ui.activities

import android.R
import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pebbles.Utils.ResourceUtils.getStringResource
import com.pebbles.data.Device


open class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

}