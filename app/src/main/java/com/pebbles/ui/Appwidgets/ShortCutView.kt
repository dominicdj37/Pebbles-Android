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
    var scrollToTeamFormationComponent: ((String?) -> Unit)? = null
    var beforeNavigationToWebView: (() -> Unit)? = null
    //endregion

    fun init(context: Context?) {
        val layout = LayoutInflater.from(context).inflate(R.layout.custom_shortcut_view, this)


    }
}
