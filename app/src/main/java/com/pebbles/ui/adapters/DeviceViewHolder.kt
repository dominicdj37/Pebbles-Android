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
    private val deviceButton: ImageView = itemView.findViewById(R.id.deviceButton)
    private val deviceImageView: ImageView = itemView.findViewById(R.id.deviceImageView)
    private val deviceSettingsImageView: ImageView = itemView.findViewById(R.id.deviceSettingsImageView)



    fun bindTo(dataHolder: DeviceDataHolder, onDeviceListClickListener: DevicesAdapter.DeviceListClickListener) {

        deviceImageView.assignImageFromUrl(dataHolder.device.imageUrl, isCircleCrop = true)

        deviceNameTextView.text = dataHolder.device.name
        automaticStatusTextView.text = if (dataHolder.device.isAuto) { "Automatic" } else { "Manual" }
        deviceButton.setOnClickListener {
            onDeviceListClickListener.onDeviceSwitchClicked(dataHolder.device)
        }
        deviceSettingsImageView.setOnClickListener {
            onDeviceListClickListener.onDeviceAddShortcutClicked(dataHolder.device)
        }

    }
}