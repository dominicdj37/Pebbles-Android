package com.pebbles.ui.adapters

import android.graphics.Color
import android.view.View
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.pebbles.R
import com.pebbles.core.Repo
import java.util.*
import kotlin.collections.ArrayList

class TempGraphComponentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val lineChart: LineChart = itemView.findViewById(R.id.graphChart)
    private val chartCalender: CalendarView = itemView.findViewById(R.id.chartCalender)
    private val calenderButton: ImageView = itemView.findViewById(R.id.calenderButton)
    private val opaqueView: View = itemView.findViewById(R.id.opaqueView)

    fun bindTo(
        dataHolder: TempGraphComponentDataHolder,
        onDeviceListClickListener: DevicesAdapter.DeviceListClickListener
    ) {
        initButtonAndView(onDeviceListClickListener)
        initChartWithData(dataHolder)
    }

    private fun initChartWithData(dataHolder: TempGraphComponentDataHolder) {
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);

        val entries = ArrayList<Entry>()
        val xLables = arrayListOf<String>()
        var xValues = 0f
        var upperLimit = 0f
        dataHolder.data.forEach {
            val labels = it.first.split(":")

            val amPm = if (labels[0].toInt() >= 12) {
                "PM"
            } else {
                "AM"
            }
            val hour = if (labels[0].toInt() > 12) {
                12 - labels[0].toInt()
            } else {
                labels[0].toInt()
            }
            xLables.add("$hour:${labels[0]} $amPm")

            val time = it.first.replace(":", "").toFloat()

            entries.add(Entry(time, it.second.toFloat()))
            upperLimit = time
        }

        val vl = LineDataSet(entries, " ")
        vl.setDrawValues(false)
        vl.setDrawFilled(true)
        vl.fillColor = R.color.colorPrimary
        vl.setDrawIcons(false);
        vl.color = Color.DKGRAY;
        vl.setCircleColor(Color.DKGRAY);
        vl.lineWidth = 1f;
        vl.circleRadius = 1f;
        vl.setDrawCircleHole(false);
        vl.valueTextSize = 9f;
        vl.setDrawFilled(true);
        vl.formLineWidth = 1f;
        lineChart.xAxis.labelRotationAngle = 0f

        lineChart.data = LineData(vl)

        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.axisMaximum = upperLimit + 0.1f

        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = MyValueFormatter()

        lineChart.description.text = "Temperature data in degree Celsius"
        lineChart.setNoDataText("No Temperature Data")
        lineChart.animateX(1800, Easing.EaseInExpo)
    }

    private fun initButtonAndView(onDeviceListClickListener: DevicesAdapter.DeviceListClickListener) {
        chartCalender.visibility = View.GONE
        opaqueView.visibility = View.GONE
        calenderButton.setOnClickListener {
            if (chartCalender.visibility == View.GONE) {
                chartCalender.visibility = View.VISIBLE
                opaqueView.visibility = View.VISIBLE
            } else {
                chartCalender.visibility = View.GONE
                opaqueView.visibility = View.GONE
            }
        }

        if (Repo.selectedDay.isNotBlank()) {
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
            val msg =
                "Loading temperature data on " + dayOfMonth + "/" + Repo.months[month] + "/" + year
            Toast.makeText(chartCalender.context, msg, Toast.LENGTH_SHORT).show()
            chartCalender.visibility = View.GONE
            opaqueView.visibility = View.GONE
            onDeviceListClickListener.onGraphDataDateSelected(
                dayOfMonth.toString(),
                Repo.months[month],
                year.toString()
            )
        }

    }


}


class MyValueFormatter : ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {

        val valueString = value.toInt().toString()
        var hour = "00"
        var min = "00"
        var second = "00"

        if (valueString.length == 6) {
            hour = "${valueString[0]}${valueString[1]}"
            min = "${valueString[2]}${valueString[3]}"
            second = "${valueString[4]}${valueString[4]}"
        }

        if (valueString.length == 5) {
            hour = "0${valueString[0]}"
            min = "${valueString[1]}${valueString[2]}"
            second = "${valueString[3]}${valueString[4]}"
        }
        if (valueString.length == 4) {
            min = "${valueString[0]}${valueString[1]}"
            second = "${valueString[2]}${valueString[3]}"
        }
        if (valueString.length == 3) {
            min = "0${valueString[0]}"
            second = "${valueString[1]}${valueString[2]}"
        }
        if (valueString.length == 2) {
            second = "${valueString[0]}${valueString[1]}"
        }
        if (valueString.length == 1) {
            second = "0${valueString[0]}"
        }

        var amPm = if (hour.toInt() >= 12) {
            "PM"
        } else {
            "AM"
        }
        val hourInt = if (hour.toInt() > 12) {
            hour.toInt() - 12
        } else {
            hour.toInt()
        }
        var hourString = hourInt.toString()
        if (hourString == "0") {
            hourString = "12"
            amPm = "AM"
        }
        return "$hourString:$min $amPm  "
    }

}