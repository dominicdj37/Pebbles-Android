package com.pebbles.ui.Appwidgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.pebbles.R
import com.pebbles.Utils.ResourceUtils
import com.pebbles.core.assignImageFromUrl
import com.pebbles.data.Device

class ShortCutView : ConstraintLayout {

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    //region views
    private lateinit var shortcutBackLayout: ConstraintLayout
    private lateinit var backLayout: ConstraintLayout
    private lateinit var shortcutDeviceImage: ImageView
    private lateinit var shortcutmask: ImageView

    private lateinit var addShortCut: ConstraintLayout

    private lateinit var shortcutdeviceNameTextView: TextView


    private lateinit var device1SettingsImageView: ImageView

    //end region

    //region click listeners
    var onSwitch: ((Device) -> Unit)? = null
    var onAddClicked: ((String) -> Unit)? = null
    var onRemoveClicked: ((Device, String) -> Unit)? = null
    //endregion

    fun init(context: Context?) {
        val layout = LayoutInflater.from(context).inflate(R.layout.custom_shortcut_view, this)
        shortcutBackLayout = layout.findViewById(R.id.shortcutBackLayout)
        backLayout = layout.findViewById(R.id.backLayout)

        shortcutDeviceImage = layout.findViewById(R.id.shortcutDeviceImage)
        addShortCut = layout.findViewById(R.id.addShortCut)
        shortcutdeviceNameTextView = layout.findViewById(R.id.shortcutdeviceNameTextView)
        device1SettingsImageView = layout.findViewById(R.id.device1SettingsImageView)
        setAddDeviceLayout()
    }

    fun setAddDeviceLayout() {
        shortcutBackLayout.visibility = View.GONE
        backLayout.visibility = View.GONE
        addShortCut.visibility = View.VISIBLE
        shortcutdeviceNameTextView.visibility = View.GONE
        device1SettingsImageView.visibility = View.GONE
        shortcutDeviceImage.visibility = View.GONE
        addShortCut.setOnClickListener {
            onAddClicked?.invoke(this.tag.toString())
        }

    }

    fun setDevice(device: Device) {
        shortcutBackLayout.visibility = View.VISIBLE
        backLayout.visibility = View.VISIBLE
        addShortCut.visibility = View.GONE
        shortcutdeviceNameTextView.visibility = View.VISIBLE
        device1SettingsImageView.visibility = View.VISIBLE
        shortcutDeviceImage.visibility = View.VISIBLE


        if(device.state == 1) {

            shortcutDeviceImage.setImageDrawable( if(device.type == "light") {
                ResourceUtils.getDrawableResource(R.drawable.ic_light_bulb)
            } else {
                ResourceUtils.getDrawableResource(R.drawable.ic_water_filter)
            })
            backLayout.setBackgroundResource(R.drawable.bg_rounded_corner)
        } else {
            shortcutDeviceImage.setImageDrawable( if(device.type == "light") {
                ResourceUtils.getDrawableResource(R.drawable.ic_light_bulb_off)
            } else {
                ResourceUtils.getDrawableResource(R.drawable.ic_water_filter_off)
            })
            backLayout.setBackgroundResource(R.color.transparent)
        }

        shortcutdeviceNameTextView.text = device.name
        shortcutBackLayout.setOnClickListener {
            onSwitch?.invoke(device)
        }
        device1SettingsImageView.setOnClickListener {
            onRemoveClicked?.invoke(device,this.tag.toString())
        }


    }
}
