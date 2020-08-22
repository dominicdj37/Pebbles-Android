package com.pebbles.ui.Custom

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.pebbles.R
import com.pebbles.Utils.ResourceUtils.getColorResource

class WaterLevelMeter @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private val holePath = Path()
    var meterTop = 0f
    var meterLeft = 0f
    var meterWidth = 100f
    var meterHeight = 400f


    val basePaint = Paint().apply {
        color = getColorResource(R.color.white_partial_transparent_2)!!
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas?) {



        holePath.reset()
        holePath.addRoundRect(meterLeft ,meterTop, meterLeft + meterWidth, meterTop + meterHeight, meterWidth,meterWidth, Path.Direction.CCW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas?.clipOutPath(holePath)
        } else {
            canvas?.clipPath(holePath, Region.Op.DIFFERENCE)
        }

        canvas?.drawRect(0f,0f,width.toFloat(),height.toFloat(), basePaint)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        meterLeft = getXPercentageOfY(50, w).toFloat()
        meterTop = getXPercentageOfY(20, h).toFloat()
        meterWidth = getXPercentageOfY(20, w).toFloat()
        meterHeight = getXPercentageOfY(70, h).toFloat()
        super.onSizeChanged(w, h, oldw, oldh)

    }


    fun getXPercentageOfY(x: Int, y: Int): Float {
        return (x/100f)*y
    }


}
