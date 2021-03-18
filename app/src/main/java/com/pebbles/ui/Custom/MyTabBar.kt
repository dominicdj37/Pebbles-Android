package com.pebbles.ui.Custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.pebbles.R
import com.pebbles.Utils.ResourceUtils


class MyTabBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    var tabList: ArrayList<Tab> = arrayListOf()
    var selectedTab: Tab? = null
    var selectedX = 0
    var selectedTextY = 0
    var selectedTextAlpha = 255
    var iconSize = 60
    var onTabClicked:((Tab) -> Unit)? = null

    private lateinit var bitmapSelectionBack:Bitmap
    private lateinit var textRect: Rect

    var mHeight = 0
    var mWidth = 0

    private val basePaint = Paint().apply {
        color = ResourceUtils.getColorResource(R.color.colorPrimary)!!
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val bitmapPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        alpha = 10
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = ResourceUtils.getColorResource(R.color.colorAccent)!!
        style = Paint.Style.FILL
        isAntiAlias = true
        textSize = 27f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }


    fun addTab(tab: Tab) {
        if (selectedTab == null) {
            selectedTab = tab

        }
        tabList.add(tab)
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        drawTabs(canvas)
        drawSelection(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mHeight = height
        mWidth = width
        iconSize = height / 3

        selectedTextY = height/2 + iconSize/2

        bitmapSelectionBack = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        super.onSizeChanged(w, h, oldw, oldh)
    }

    private fun drawSelection(canvas: Canvas?) {
        if(tabList.isEmpty()) {
            return
        }

        if (selectedTab?.centerx == 0) {
            selectedTab?.centerx = tabList.first().centerx
            selectedTab?.centery = tabList.first().centery
            selectedX = selectedTab?.centerx ?: 0
        }


        //canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(),  paintShadow);
        canvas?.drawRect(0f, 1f, width.toFloat(), height.toFloat(),  basePaint);

        drawTabs(canvas)
    }

    private fun drawTabs(canvas: Canvas?) {
        if(tabList.isEmpty()) {
            return
        }

        if(selectedTab == null) {
            selectedTab = tabList.first()
        }

        val splitWidth = mWidth/tabList.size
        //calculate offset
        val topOffset = (mHeight/2 - iconSize/2).toFloat()
        val leftOffset = (splitWidth - iconSize) / 2
        var leftPointer = 0

        tabList.forEach {tab->
            tab.centerx = (leftPointer + (leftPointer + splitWidth) )/2
            tab.centery = mHeight - mHeight/3
            val iconLeft = leftPointer + leftOffset
            tab.startX = iconLeft
            tab.endX = iconLeft + iconSize

            var top = topOffset.toFloat()
            var text = ""
            if(tab == selectedTab) {
                bitmapPaint.alpha = 255
                top = iconSize/2.toFloat()
                text = tab.name
                textRect = Rect()
                textPaint.getTextBounds(tab.name,0, tab.name.length, textRect)
            } else {
                bitmapPaint.alpha = 100
                textPaint.getTextBounds("",0, 0, textRect)
            }

            textPaint.alpha = selectedTextAlpha

            val bitmap = ContextCompat.getDrawable(context, tab.iconID)!!.toBitmap(iconSize, iconSize)
            canvas?.drawBitmap(bitmap, iconLeft.toFloat(), top, bitmapPaint)
            canvas?.drawText(text,tab.centerx.toFloat(), selectedTextY.toFloat() + 10, textPaint)
            leftPointer += splitWidth
        }

        if (selectedX == 0) {
            selectedX = selectedTab?.centerx ?: 0
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val clickedTab = tabList.find { tab -> tab.startX < x && x < tab.endX}

                if(clickedTab!= null && clickedTab != selectedTab) {
                    animateTextPosition(clickedTab)
                    animateTextAlpha(clickedTab)
                }
                animateToTab(clickedTab)

                return true
            }
        }
        return true
    }

    fun animateTextAlpha(clickedTab: Tab?) {
        val valueAnimator = ValueAnimator.ofInt(0, 255)
        valueAnimator.addUpdateListener {
            selectedTextAlpha = it.animatedValue as Int
            invalidate()
        }
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 250

        valueAnimator.start()
    }

    fun animateTextPosition(clickedTab: Tab?) {
        val valueAnimator = ValueAnimator.ofFloat(0f, height/2.toFloat() - iconSize/2 )
        valueAnimator.addUpdateListener {
            selectedTextY = height - (it.animatedValue as Float).toInt()
            invalidate()
        }
        valueAnimator.interpolator = BounceInterpolator()
        valueAnimator.duration = 250

        valueAnimator.start()
    }


    fun animateToTab(clickedTab: Tab?) {
        clickedTab?.let {tab ->
            onTabClicked?.invoke(tab)
            selectedTab = clickedTab
            val valueAnimator = ValueAnimator.ofFloat(selectedX.toFloat(), tab.centerx.toFloat())
            valueAnimator.addUpdateListener {
                selectedX = (it.animatedValue as Float).toInt()
                invalidate()
            }
            valueAnimator.interpolator = OvershootInterpolator()
            valueAnimator.duration = 1000

            valueAnimator.start()

        }


    }

}


data class Tab (
    var id:Int = 0,
    var iconID:Int = R.drawable.ic_my_tanks, //default icon
    var name:String = "",
    var centerx:Int = 0,
    var centery:Int =0,
    var startX:Int = 0,
    var endX:Int = 0
)