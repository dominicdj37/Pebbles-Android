package com.pebbles.ui.adapters

import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.pebbles.R

class AddDeviceViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val addDeviceButton: ImageButton = itemView.findViewById(R.id.addDeviceButton)

    fun bindTo(sectionTitleDataHolder: AddDeviceDataHolder, ListInteractionsListener: CommonListAdapter.ListInteractionsListener) {
        addDeviceButton.setOnClickListener {
            ListInteractionsListener.onAddPebbleClicked()
        }
    }
}