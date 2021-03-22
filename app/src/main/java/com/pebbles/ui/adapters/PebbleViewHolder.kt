package com.pebbles.ui.adapters

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.pebbles.R
import com.pebbles.Utils.ColorUtils

class PebbleViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val parentLayout = itemView.findViewById<ConstraintLayout>(R.id.parentLayout)
    private val pebbleName = itemView.findViewById<TextView>(R.id.pebbleName)
    private val pebbleStatus = itemView.findViewById<TextView>(R.id.pebbleStatus)
    private val pebbleOwnerName = itemView.findViewById<TextView>(R.id.pebbleOwnerName)
    private val pebbleDeviceCount = itemView.findViewById<TextView>(R.id.pebbleDeviceCount)
    private val pebbleUsersCount = itemView.findViewById<TextView>(R.id.pebbleUsersCount)
    private val pebbleUptime = itemView.findViewById<TextView>(R.id.pebbleUptime)
    private val sharePebbleIcon = itemView.findViewById<ImageView>(R.id.sharePebbleIcon)

    fun bindTo(pebbleDataHolder: PebbleDataHolder, listInteractionsListener: CommonListAdapter.ListInteractionsListener) {

        parentLayout.setBackgroundColor(ColorUtils.RandomColors().color)

        pebbleName.text = pebbleDataHolder.pebble.pebbleName ?: ""
        pebbleStatus.text = pebbleDataHolder.pebble.status ?: ""
        val ownerText = "Owner: ${pebbleDataHolder.pebble.owner?.username ?: ""}"
        pebbleOwnerName.text = ownerText
        pebbleDeviceCount.text = (pebbleDataHolder.pebble.deviceCount ?: "0").toString()
        pebbleUsersCount.text = pebbleDataHolder.pebble.users.size.toString()
        pebbleUptime.text = pebbleDataHolder.pebble.upTime ?: "Not Available"

        if(pebbleDataHolder.pebble.isMine())
        {
            sharePebbleIcon.visibility = View.VISIBLE
            sharePebbleIcon.setOnClickListener {
                pebbleDataHolder.pebble.id?.let { id -> listInteractionsListener.onSharePebbleClicked(id) }
            }
        } else {
            sharePebbleIcon.visibility = View.GONE
        }


    }
}