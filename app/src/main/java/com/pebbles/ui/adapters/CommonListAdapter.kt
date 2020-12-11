package com.pebbles.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.pebbles.R
import com.pebbles.data.Device

class CommonListAdapter(
    val list: ArrayList<Any>,
    private val listInteractionListener: ListInteractionsListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var lastPosition = -1


    override fun getItemViewType(position: Int): Int {
        return when(list[position]) {
            is DeviceDataHolder -> R.layout.devices_item_layout
            is AddDeviceDataHolder -> R.layout.add_device_item_layout
            is TempGraphComponentDataHolder -> R.layout.graph_item_layout
            is ChatDataHolder -> R.layout.chat_item_layout
            is FriendDataHolder -> R.layout.friend_item_layout
            else -> R.layout.add_device_item_layout
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.devices_item_layout -> DeviceViewHolder(
                layoutView
            )
            R.layout.add_device_item_layout -> AddDeviceViewHolder(
                layoutView
            )
            R.layout.graph_item_layout -> TempGraphComponentViewHolder(
                layoutView
            )
            R.layout.chat_item_layout -> ChatViewHolder(layoutView)
            R.layout.friend_item_layout -> FriendViewHolder(layoutView)

            else -> AddDeviceViewHolder(layoutView)
        }
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(list[position]) {
            is DeviceDataHolder -> (holder as DeviceViewHolder).bindTo(list[position] as DeviceDataHolder, listInteractionListener)
            is AddDeviceDataHolder -> (holder as AddDeviceViewHolder).bindTo(list[position] as AddDeviceDataHolder, listInteractionListener)
            is TempGraphComponentDataHolder -> (holder as TempGraphComponentViewHolder).bindTo(list[position] as TempGraphComponentDataHolder, listInteractionListener)
            is ChatDataHolder -> (holder as ChatViewHolder).bindTo(list[position] as ChatDataHolder, listInteractionListener)
            is FriendDataHolder -> (holder as FriendViewHolder).bindTo(list[position] as FriendDataHolder, listInteractionListener)

        }

    }


    interface ListInteractionsListener {
        fun onDeviceSwitchClicked(device: Device)
        fun onAddDeviceClicked()
        fun onDeviceAddShortcutClicked(device: Device)
        fun onGraphDataDateSelected(day: String, month: String, year: String)
    }

}
