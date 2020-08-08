package com.pebbles.ui.adapters

import android.animation.Animator
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pebbles.R
import com.pebbles.Utils.ResourceUtils.getDrawableResource
import com.pebbles.core.assignImageFromUrl
import kotlin.math.hypot
import kotlin.math.max


class DeviceViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val expandBackView: View = itemView.findViewById(R.id.expandBackView)
    private val deviceNameTextView: TextView = itemView.findViewById(R.id.deviceNameTextView)
    private val automaticStatusTextView: TextView = itemView.findViewById(R.id.automaticStatusTextView)
    private val deviceButton: ImageView = itemView.findViewById(R.id.deviceButton)
    private val deviceImageView: ImageView = itemView.findViewById(R.id.deviceImageView)
    private val deviceSettingsImageView: ImageView = itemView.findViewById(R.id.deviceSettingsImageView)



    fun bindTo(dataHolder: DeviceDataHolder, onDeviceListClickListener: CommonListAdapter.DeviceListClickListener) {

        deviceImageView.assignImageFromUrl(dataHolder.device.imageUrl, isCircleCrop = true)

        deviceNameTextView.text = dataHolder.device.name
        automaticStatusTextView.text = if (dataHolder.device.isAuto) { "Automatic" } else { "Manual" }

        if(dataHolder.device.state == 1) {
            deviceButton.setImageDrawable(getDrawableResource(R.drawable.ic_power_on))
            expandBackView.visibility = View.VISIBLE
        } else {
            deviceButton.setImageDrawable(getDrawableResource(R.drawable.ic_power_off))
            expandBackView.visibility = View.INVISIBLE
        }
        deviceButton.setOnClickListener {
            onDeviceListClickListener.onDeviceSwitchClicked(dataHolder.device)
            if(dataHolder.device.state == 1) {
                animateOut()
            } else {
                animateIn()
            }

        }
        deviceSettingsImageView.setOnClickListener {
            onDeviceListClickListener.onDeviceAddShortcutClicked(dataHolder.device)
        }

    }

    private fun animateOut() {
        val x: Int = itemView.right
        val y: Int = itemView.bottom

        val startRadius = max(expandBackView.width, expandBackView.height);
        val endRadius = 0


        val anim: Animator = ViewAnimationUtils.createCircularReveal(
            expandBackView,
            x,
            y,
            startRadius.toFloat(),
            endRadius.toFloat()
        )
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                expandBackView.visibility = View.INVISIBLE
            }
            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        anim.start()
    }

    fun animateIn() {
        val x: Int = itemView.right
        val y: Int = itemView.bottom

        val startRadius = 0
        val endRadius =
            hypot(expandBackView.width.toDouble(),
                expandBackView.height.toDouble()
            ).toInt()

        val anim: Animator = ViewAnimationUtils.createCircularReveal(
            expandBackView,
            x,
            y,
            startRadius.toFloat(),
            endRadius.toFloat()
        )
        anim.start()
        expandBackView.visibility = View.VISIBLE
    }

}