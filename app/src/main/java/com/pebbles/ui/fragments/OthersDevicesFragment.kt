package com.pebbles.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pebbles.R
import com.pebbles.core.DatabaseHelper
import com.pebbles.core.Repo
import com.pebbles.data.Device
import com.pebbles.ui.adapters.AddDeviceDataHolder
import com.pebbles.ui.adapters.DeviceDataHolder
import com.pebbles.ui.adapters.DevicesAdapter
import com.pebbles.ui.adapters.DevicesAdapter.DeviceListClickListener

/**
 * A fragment representing a list of Items.
 */
class OthersDevicesFragment: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_other_devices, container, false)

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

}
