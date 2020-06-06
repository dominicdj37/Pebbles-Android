package com.pebbles.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pebbles.R
import com.pebbles.core.Repo
import com.pebbles.data.Device
import com.pebbles.ui.adapters.AddDeviceDataHolder
import com.pebbles.ui.adapters.DeviceDataHolder
import com.pebbles.ui.adapters.DevicesAdapter
import com.pebbles.ui.adapters.DevicesAdapter.DeviceListClickListener

/**
 * A fragment representing a list of Items.
 */
class DeviceFragment : Fragment(), DeviceListClickListener {

    private var columnCount = 1

    private val deviceList = arrayListOf<Any>(AddDeviceDataHolder())
    private lateinit var deviceAdapter: DevicesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_device_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                deviceAdapter = DevicesAdapter(deviceList, this@DeviceFragment)
                adapter = deviceAdapter

            }
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            DeviceFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    fun reloadDeviceList() {
        Repo.devices.takeIf { it.isNotEmpty() }?.let {
            deviceList.clear()
            it.forEach { device->
                deviceList.add(DeviceDataHolder(device))
            }
            deviceList.add(AddDeviceDataHolder())
            deviceAdapter.notifyDataSetChanged()
        }
    }

    override fun onDeviceSwitchClicked(device: Device) {

    }

    override fun onAddDeviceClicked() {

    }
}