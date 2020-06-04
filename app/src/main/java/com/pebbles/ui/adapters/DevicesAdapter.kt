package com.pebbles.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pebbles.R
import com.pebbles.data.Device

class DevicesAdapter(
    val list: ArrayList<Any>,
    private val deviceListClickListener: DeviceListClickListener,
    private val deviceRecyclerView: RecyclerView
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when(list[position]) {
            is DeviceDataHolder -> R.layout.devices_item_layout
            is AddDeviceDataHolder -> R.layout.add_device_item_layout
            else -> R.layout.add_device_item_layout
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        val width: Int = deviceRecyclerView.width
        val params: ViewGroup.LayoutParams = layoutView.layoutParams
        params.width = (((width * 0.98).toInt()))
        layoutView.layoutParams = params

        return when (viewType) {
            R.layout.devices_item_layout -> DeviceViewHolder(
                layoutView
            )
            R.layout.add_device_item_layout -> AddDeviceViewHolder(
                layoutView
            )
            else -> AddDeviceViewHolder(layoutView)
        }
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(list[position]) {
            is DeviceDataHolder -> (holder as DeviceViewHolder).bindTo(list[position] as DeviceDataHolder, deviceListClickListener)
            is AddDeviceDataHolder -> (holder as AddDeviceViewHolder).bindTo(list[position] as AddDeviceDataHolder, deviceListClickListener)
        }

    }

    interface DeviceListClickListener {
        fun onDeviceSwitchClicked(device: Device)
        fun onAddDeviceClicked()
    }


}