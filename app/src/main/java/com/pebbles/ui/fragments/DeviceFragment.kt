package com.pebbles.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pebbles.R
import com.pebbles.core.DatabaseHelper
import com.pebbles.core.Repo
import com.pebbles.data.Device
import com.pebbles.databinding.FragmentDeviceListBinding
import com.pebbles.ui.adapters.AddDeviceDataHolder
import com.pebbles.ui.adapters.DeviceDataHolder
import com.pebbles.ui.adapters.CommonListAdapter
import com.pebbles.ui.adapters.CommonListAdapter.DeviceListClickListener
import com.pebbles.ui.adapters.MotionDataHolder
import java.util.HashMap

/**
 * A fragment representing a list of Items.
 */
class DeviceFragment : Fragment(), DeviceListClickListener {

    private var columnCount = 1

    private val deviceList = arrayListOf<Any>(AddDeviceDataHolder())
    private lateinit var deviceAdapter: CommonListAdapter
    private var listener: OnDeviceTabInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDeviceTabInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnDeviceTabInteractionListener")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDeviceListBinding.inflate(inflater,container,false)
        // Set the adapter
        with(binding.list) {
            layoutManager = LinearLayoutManager(context)
            deviceAdapter = CommonListAdapter(deviceList, this@DeviceFragment)
            adapter = deviceAdapter
        }
        Log.d("Pebbles_debug", "on create view")
        return binding.root
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int): Fragment {
            Log.d("Pebbles_debug", "on new instance")
            return DeviceFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("Pebbles_debug", "on view created")
        initDeviceStateListener()
        reloadDeviceList()
    }


    fun reloadDeviceList() {
        Repo.user?.deviceSetId?.let {
            DatabaseHelper.returnDevicesForUid(it, {
                Repo.devices.takeIf { it.isNotEmpty() }?.let {
                    deviceList.clear()
                    it.forEach { device->
                        deviceList.add(DeviceDataHolder(device))
                    }
                    deviceList.add(AddDeviceDataHolder())

                    Repo.users.forEach { user ->
                        deviceList.add(MotionDataHolder(user))
                    }

                    Repo.users.forEach { user ->
                        deviceList.add(MotionDataHolder(user))
                    }
                    deviceAdapter.notifyDataSetChanged()
                }

            }, {
               //error handel
            })
        }
    }

    override fun onDeviceSwitchClicked(device: Device) {
        DatabaseHelper.switchDevice(device, {}) { }
    }

    private fun initDeviceStateListener() {
        val messageListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val portData = dataSnapshot.value as HashMap<String, Long>
                    portData.forEach { (port, state) ->
                        Repo.devices.find { device -> device.port.toString() == port[1].toString()}?.state = state.toInt()
                    }
                    Repo.devices.takeIf { it.isNotEmpty() }?.let {
                        deviceList.clear()
                        it.forEach { device ->
                            deviceList.add(DeviceDataHolder(device))
                        }
                        deviceList.add(AddDeviceDataHolder())
                        Repo.users.forEach { user ->
                            deviceList.add(MotionDataHolder(user))
                        }
                        Repo.users.forEach { user ->
                            deviceList.add(MotionDataHolder(user))
                        }
                        deviceAdapter.notifyDataSetChanged()
                        deviceAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read value
            }
        }
        Repo.user?.deviceSetId?.let { DatabaseHelper.databaseReference?.child("portData")?.child(it)?.addValueEventListener(messageListener) }

    }

    override fun onAddDeviceClicked() {

    }

    override fun onDeviceAddShortcutClicked(device: Device) {
        DatabaseHelper.addDeviceShortCut(device, {
            listener?.shortcutAdded() }, {
                //error
            })
    }

    override fun onGraphDataDateSelected(day: String, month: String, year: String) { }

    interface OnDeviceTabInteractionListener {
        fun shortcutAdded()
    }
}