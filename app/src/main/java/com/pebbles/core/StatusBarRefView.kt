package com.pebbles.core

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View



class StatusBarSpacer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private var statusHeight: Int = 60

    init {
        if (context is Activity ) {
            context.window.decorView.setOnApplyWindowInsetsListener { _, insets ->
                statusHeight = insets.stableInsetTop
                requestLayout()
                insets
            }
            context.window.decorView.requestApplyInsets()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) =
        setMeasuredDimension(0, statusHeight)
}