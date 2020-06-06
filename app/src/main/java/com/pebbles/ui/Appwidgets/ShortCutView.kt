package com.pebbles.ui.Appwidgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.pebbles.R
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
    private lateinit var shortcutDeviceImage: ImageView
    private lateinit var shortcutmask: ImageView

    private lateinit var addShortCut: ConstraintLayout

    private lateinit var shortcutdeviceNameTextView: TextView


    private lateinit var shortcutdeviceButton: Button
    private lateinit var device1SettingsImageView: ImageView

    //end region

    //region click listeners
    var onSwitch: ((Device) -> Unit)? = null
    var onAddClicked: (() -> Unit)? = null
    var onSettingsClicked: ((Device) -> Unit)? = null
    //endregion

    fun init(context: Context?) {
        val layout = LayoutInflater.from(context).inflate(R.layout.custom_shortcut_view, this)
        shortcutBackLayout = layout.findViewById(R.id.shortcutBackLayout)

        shortcutDeviceImage = layout.findViewById(R.id.shortcutDeviceImage)
        addShortCut = layout.findViewById(R.id.addShortCut)
        shortcutdeviceNameTextView = layout.findViewById(R.id.shortcutdeviceNameTextView)
        shortcutdeviceButton = layout.findViewById(R.id.shortcutdeviceButton)
        device1SettingsImageView = layout.findViewById(R.id.device1SettingsImageView)
        setAddDeviceLayout()
    }

    fun setAddDeviceLayout() {
        shortcutBackLayout.visibility = View.GONE
        addShortCut.visibility = View.VISIBLE
        shortcutdeviceNameTextView.visibility = View.GONE
        shortcutdeviceButton.visibility = View.GONE
        device1SettingsImageView.visibility = View.GONE

        addShortCut.setOnClickListener {
            onAddClicked?.invoke()
        }
    }

    fun setDevice(device: Device) {
        shortcutBackLayout.visibility = View.VISIBLE
        addShortCut.visibility = View.GONE
        shortcutdeviceNameTextView.visibility = View.VISIBLE
        shortcutdeviceButton.visibility = View.VISIBLE
        device1SettingsImageView.visibility = View.VISIBLE


        shortcutDeviceImage.assignImageFromUrl(device.imageUrl)
        shortcutdeviceNameTextView.text = device.name
        shortcutdeviceButton.setOnClickListener {
            onSwitch?.invoke(device)
        }
        device1SettingsImageView.setOnClickListener {
            onSettingsClicked?.invoke(device)
        }


    }
}
