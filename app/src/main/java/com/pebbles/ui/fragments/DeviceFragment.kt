package com.pebbles.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pebbles.R
import com.pebbles.core.DatabaseHelper
import com.pebbles.core.Repo
import com.pebbles.data.Device
import com.pebbles.ui.adapters.AddDeviceDataHolder
import com.pebbles.ui.adapters.CommonListAdapter
import com.pebbles.ui.adapters.CommonListAdapter.ListInteractionsListener
import com.pebbles.ui.adapters.DeviceDataHolder
import com.pebbles.ui.adapters.PebbleDataHolder
import com.pebbles.ui.viewModels.HomeViewModel
import java.util.*


/**
 * A fragment representing a list of Items.
 */
class DeviceFragment : BaseFragment(), ListInteractionsListener {

    private lateinit var pageViewModel: HomeViewModel

    private var columnCount = 1

    private val deviceList = arrayListOf<Any>(AddDeviceDataHolder())
    private lateinit var deviceAdapter: CommonListAdapter
    private var listener: OnDeviceTabInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pageViewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)
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
        val view = inflater.inflate(R.layout.fragment_device_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                deviceAdapter = CommonListAdapter(deviceList, this@DeviceFragment)
                adapter = deviceAdapter

            }

        }
        Log.d("Pebbles_debug", "on create view")
        return view
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


        getPebblesList()

//        initDeviceStateListener()
//        reloadDeviceList()
    }


    private fun getPebblesList() {
        pageViewModel.pebblesLiveData.observe(this, androidx.lifecycle.Observer {
            checkResponse(it, onSuccess = {
                reloadPebbelsList()
            }, onRetry = {
                //todo not needed. change to swipe refresh
            })
        })
        pageViewModel.getPebbles()
    }

    private fun reloadPebbelsList() {
        pageViewModel.pebbles?.my_pebbles?.let {arrayList ->
            arrayList.forEach { pebble ->
                deviceList.clear()
                deviceList.add(PebbleDataHolder(pebble))
            }
            deviceList.add(AddDeviceDataHolder())
            deviceAdapter.notifyDataSetChanged()
        }
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