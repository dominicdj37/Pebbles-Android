package com.pebbles.ui.adapters

import android.view.View
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.pebbles.R
import com.pebbles.core.Repo
import java.util.*
import kotlin.collections.ArrayList

class TempGraphComponentViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val lineChart: LineChart = itemView.findViewById(R.id.graphChart)
    private val chartCalender: CalendarView = itemView.findViewById(R.id.chartCalender)
    private val calenderButton: ImageView = itemView.findViewById(R.id.calenderButton)

    fun bindTo(dataHolder: TempGraphComponentDataHolder, onDeviceListClickListener: DevicesAdapter.DeviceListClickListener) {
        initButtonAndView(onDeviceListClickListener)
        initChartWithData(dataHolder)
    }

    private fun initChartWithData(dataHolder: TempGraphComponentDataHolder) {
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);

        val entries = ArrayList<Entry>()
        var upperLimit = 0f
        dataHolder.data.forEach {
            val time = it.first.replace(":","").toFloat()
            entries.add(Entry(time, it.second.toFloat()))
            upperLimit = time
        }

        val vl = LineDataSet(entries, "Time")
        vl.setDrawValues(false)
        vl.setDrawFilled(true)
        vl.lineWidth = 3f
        vl.fillColor = R.color.colorPrimary2
        vl.fillAlpha = R.color.colorPrimaryDark

        lineChart.xAxis.labelRotationAngle = 0f

        lineChart.data = LineData(vl)

        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.axisMaximum = upperLimit + 0.1f
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)

        lineChart.description.text = "Temperature data in degree Celsius"
        lineChart.setNoDataText("No Temperature Data")
        lineChart.animateX(1800, Easing.EaseInExpo)
    }

    private fun initButtonAndView(onDeviceListClickListener: DevicesAdapter.DeviceListClickListener) {
        chartCalender.visibility = View.GONE
        calenderButton.setOnClickListener {
            if (chartCalender.visibility == View.GONE) {
                chartCalender.visibility = View.VISIBLE
            } else {
                chartCalender.visibility = View.GONE
            }
        }

        if(Repo.selectedDay.isNotBlank()) {
            val calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Repo.selectedYear.toInt());
            calendar.set(Calendar.MONTH, Repo.months.indexOf(Repo.selectedMonth))
            calendar.set(Calendar.DAY_OF_MONTH, Repo.selectedDay.toInt());
            chartCalender.date = calendar.timeInMillis
        } else {
            chartCalender.date = Calendar.getInstance().timeInMillis
        }

        chartCalender.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.
            val msg = "Loading temperature data on " + dayOfMonth + "/" + Repo.months[month] + "/" + year
            Toast.makeText(chartCalender.context, msg, Toast.LENGTH_SHORT).show()
            chartCalender.visibility = View.GONE
            onDeviceListClickListener.onGraphDataDateSelected(dayOfMonth.toString(), Repo.months[month], year.toString())
        }

    }




}