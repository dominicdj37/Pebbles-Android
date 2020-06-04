package com.pebbles.ui.adapters

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pebbles.R
import com.pebbles.core.assignImageFromUrl

class DeviceViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val deviceNameTextView: TextView = itemView.findViewById(R.id.deviceNameTextView)
    private val automaticStatusTextView: TextView = itemView.findViewById(R.id.automaticStatusTextView)
    private val deviceButton: Button = itemView.findViewById(R.id.deviceButton)
    private val deviceImageView: ImageView = itemView.findViewById(R.id.deviceImageView)



    fun bindTo(dataHolder: DeviceDataHolder, onDeviceListClickListener: DevicesAdapter.DeviceListClickListener) {

        deviceImageView.assignImageFromUrl(dataHolder.device.deviceImageUrl)

        deviceNameTextView.text = dataHolder.device.deviceName
        automaticStatusTextView.text = if (dataHolder.device.isAutomated) { "Automatic" } else { "Manual" }
        deviceButton.text = if(dataHolder.device.isDeviceOn) { "TURN OFF" } else { "TURN ON" }
        deviceButton.setOnClickListener {
            onDeviceListClickListener.onDeviceSwitchClicked(dataHolder.device)
        }

    }
}