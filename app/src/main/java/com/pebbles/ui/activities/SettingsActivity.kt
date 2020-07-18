package com.pebbles.ui.activities

import android.os.Bundle
import com.pebbles.R
import com.pebbles.core.sessionUtils
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initializeToolbar()


        fingerPrintSwitch.setIconEnabled(sessionUtils.getBiometricEnabledFlag())
        fingerPrintSwitchLayout.setOnClickListener {
            fingerPrintSwitch.switchState(true)
            sessionUtils.setBiometricEnabledFlag(fingerPrintSwitch.isIconEnabled)
        }

        notificationSwitch.setIconEnabled(sessionUtils.getUserAlertsOn())
        pushNotificationSwitchLayout.setOnClickListener {
            notificationSwitch.switchState(true)
            sessionUtils.setUserAlertsOn(fingerPrintSwitch.isIconEnabled)
        }

    }

    private fun initializeToolbar() {
        settingsToolbar.setupToolBar("Settings") {
            onBackPressed()
        }
    }
}