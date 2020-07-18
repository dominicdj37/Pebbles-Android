package com.pebbles.ui.Appwidgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.pebbles.R
import com.pebbles.data.Device

class ToolBarLayout : ConstraintLayout {

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
    private lateinit var backButton: ImageView
    private lateinit var toolBarTitleTextView: TextView
    //end region

    //region click listeners
    var onBackPressed: (() -> Unit)? = null
    var onAddClicked: ((String) -> Unit)? = null
    //endregion

    fun init(context: Context?) {
        val layout = LayoutInflater.from(context).inflate(R.layout.layout_toolbar, this)
        backButton = layout.findViewById(R.id.backButton)
        toolBarTitleTextView = layout.findViewById(R.id.toolBarTitleTextView)
    }

    fun setupToolBar(title: String, onBackPressed: (() -> Unit)? = null) {
        toolBarTitleTextView.text = title
        backButton.setOnClickListener {
            onBackPressed?.invoke()
        }
    }

}