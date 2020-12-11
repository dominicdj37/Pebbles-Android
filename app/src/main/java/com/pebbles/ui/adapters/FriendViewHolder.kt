package com.pebbles.ui.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pebbles.R
import com.pebbles.core.assignImageFromUrl

class FriendViewHolder  (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
    private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)

    fun bindTo(dataHolder: FriendDataHolder, ListInteractionsListener: CommonListAdapter.ListInteractionsListener) {

        profileImageView.assignImageFromUrl(dataHolder.user.profilePhotoUrl, isCircleCrop = true)
        nameTextView.text = dataHolder.user.name
    }

}