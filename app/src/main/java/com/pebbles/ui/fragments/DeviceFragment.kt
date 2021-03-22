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
import com.pebbles.ui.popups.AddPebblePopup
import com.pebbles.ui.popups.SharePebblePopup
import com.pebbles.ui.viewModels.HomeViewModel
import java.util.*


/**
 * A fragment representing a list of Items.
 */
class DeviceFragment : BaseFragment(), ListInteractionsListener {

    private var popupAdd: AddPebblePopup? = null
    private var popupShare: SharePebblePopup? = null
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

        initListObservers()
    }

    private fun initListObservers() {
        pageViewModel.pebblesLiveData.observe(this, androidx.lifecycle.Observer {
            checkResponse(it, onSuccess = {
                reloadPebblesList()
            }, onRetry = {
                //todo not needed. change to swipe refresh
            })
        })
    }


    private fun getPebblesList() {
        pageViewModel.getPebbles()
    }

    private fun reloadPebblesList() {
        pageViewModel.pebbles?.my_pebbles?.let {arrayList ->
            deviceList.clear()
            arrayList.forEach { pebble ->
                deviceList.add(PebbleDataHolder(pebble))
            }
            deviceList.add(AddDeviceDataHolder())
            deviceAdapter.notifyDataSetChanged()
        }
    }

    override fun onAddPebbleClicked() {
        popupAdd = AddPebblePopup(context!!, object: AddPebblePopup.PopupListener{
            override fun onCancelClicked() {}

            override fun onNextClicked(name: String, key: String) {
                connectNewPebble(name, key)
            }
        })
    }

    private fun connectNewPebble(name: String, key: String) {
        pageViewModel.connectNewPebble(name, key)
    }


    override fun onSharePebbleClicked(id: Int) {
        popupShare = SharePebblePopup(context!!, object: SharePebblePopup.PopupListener {
            override fun onCancelClicked() {

            }

            override fun onNextClicked(username: String) {
                sharePebble(username, id)
            }

        } )
    }

    private fun sharePebble(username: String, id: Int) {
        pageViewModel.sharePebble(username = username, id = id)
    }









    override fun onDeviceAddShortcutClicked(device: Device) {}
    override fun onGraphDataDateSelected(day: String, month: String, year: String) { }
    interface OnDeviceTabInteractionListener { fun shortcutAdded() }
    override fun onDeviceSwitchClicked(device: Device) { }


}