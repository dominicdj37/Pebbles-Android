package com.pebbles.backgroundServices

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.annotation.RequiresApi
import com.pebbles.core.DatabaseHelper
import com.pebbles.core.Repo
import com.pebbles.data.Device

class MyLightTileService: TileService() {

    override fun onClick() {
        super.onClick()
        Log.d("devices:", Repo.devices.toString())

        Repo.devices.takeIf { it.isNotEmpty() }?.let {
            it.find { device -> device.port == 2 }?.let { device ->
                switchDevice(device)
                if(device.state == 1) { //switch last state
                    qsTile.state = Tile.STATE_INACTIVE
                    qsTile.label = "Light Off"
                } else {
                    qsTile.state = Tile.STATE_ACTIVE
                    qsTile.label = "Light On"
                }
            }
        }



        // Update looks
        qsTile.updateTile()
    }
    private fun switchDevice(device: Device) {
        if(device.state != -1) {
            DatabaseHelper.switchDevice(device, {

            } ) {
                //error
            }
        }
    }
    override fun onTileRemoved() {
        super.onTileRemoved()

        // Do something when the user removes the Tile
    }

    override fun onTileAdded() {
        super.onTileAdded()
        // Update state

        if(Repo.devices.isEmpty()){
            qsTile.state = Tile.STATE_UNAVAILABLE
            qsTile.label = "Light"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                qsTile.subtitle = "Sign in to enable"
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                qsTile.subtitle = ""
            }
        }
        // Update looks
        qsTile.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        Repo.devices.takeIf { it.isNotEmpty() }?.let {
            it.find { device -> device.port == 2 }?.let {device ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    qsTile.subtitle = ""
                }

                if(device.state == 1) {
                    qsTile.state = Tile.STATE_ACTIVE
                    qsTile.label = "Light On"
                } else {
                    qsTile.state = Tile.STATE_INACTIVE
                    qsTile.label = "Light Off"
                }


            }
        } ?: let {
            qsTile.state = Tile.STATE_UNAVAILABLE
            qsTile.label = "Light"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                qsTile.subtitle = "Sign in to enable"
            }
        }

        // Update looks
        qsTile.updateTile()
        // Called when the Tile becomes visible
    }

    override fun onStopListening() {
        super.onStopListening()

        // Called when the tile is no longer visible
    }

}