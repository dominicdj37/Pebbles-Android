package com.pebbles.ui.fragments

import android.os.Bundle
import android.util.Log
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
import com.pebbles.ui.adapters.CommonListAdapter
import com.pebbles.ui.adapters.CommonListAdapter.DeviceListClickListener
import com.pebbles.ui.adapters.TempGraphComponentDataHolder
import java.util.*

/**
 * A fragment representing a list of Items.
 */
class GraphFragment : Fragment(), DeviceListClickListener {

    private var columnCount = 1

    private val graphList = arrayListOf<Any>()
    private lateinit var graphsAdapter: CommonListAdapter

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
                graphsAdapter = CommonListAdapter(graphList, this@GraphFragment)
                adapter = graphsAdapter
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
            return GraphFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("Pebbles_debug", "on view created")
        fetchTodayTempData()
    }

    private fun reloadGraphList() {
        graphList.clear()
        graphList.add(TempGraphComponentDataHolder(Repo.currentTempGraphData))
        graphsAdapter.notifyDataSetChanged()
    }


    private fun fetchTodayTempData() {
        val calender = Calendar.getInstance()
        calender.time = Date()
        getTemperatureDataFromDate(
            calender.get(Calendar.DAY_OF_MONTH).toString(),
            Repo.months[calender.get(Calendar.MONTH)],
            calender.get(Calendar.YEAR).toString()
        )
    }


    private fun getTemperatureDataFromDate(day: String, month: String, year: String) {
        Repo.selectedDay = day
        Repo.selectedMonth = month
        Repo.selectedYear = year
        DatabaseHelper.returnTempDataFor(day, month, year) {
            Repo.currentTempGraphData = it
            reloadGraphList()
        }
    }


    override fun onDeviceSwitchClicked(device: Device) {
    }

    override fun onAddDeviceClicked() {
    }

    override fun onDeviceAddShortcutClicked(device: Device) {
    }

    override fun onGraphDataDateSelected(day: String, month: String, year: String) {
        getTemperatureDataFromDate(day, month, year)
    }
}