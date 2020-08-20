package com.pebbles.ui.Custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.pebbles.R
import com.pebbles.data.Device


class MyTabBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    var tabList: ArrayList<Tab> = arrayListOf()
    var selectedTab: Tab? = null
    var selectedX = 0

    var onTabClicked:((Tab) -> Unit)? = null

    private var tempSelectionCanvas: Canvas? = null
    private lateinit var paintSelection: Paint
    private val noPaint = Paint()
    private lateinit var transparentPaint: Paint
    private lateinit var bitmapSelectionBack:Bitmap

    var mHeight = 0
    var mWidth = 0

    var mLeft = 0f
    var mBottom = 0f
    var mTop = 0f
    var mRight = 0f

    val basePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL

        isAntiAlias = true
    }

    val bitmaPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        alpha = 10
        isAntiAlias = true
    }


    init {


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


        bitmapSelectionBack = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        tempSelectionCanvas = Canvas(bitmapSelectionBack)
        paintSelection = Paint()
        paintSelection.color = Color.WHITE
        transparentPaint = Paint()
        transparentPaint.color = Color.TRANSPARENT
        transparentPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

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


        tempSelectionCanvas?.drawRoundRect(0f, 0f, tempSelectionCanvas?.width?.toFloat() ?: 0f, tempSelectionCanvas?.height?.toFloat() ?: 0f, 100f,100f, paintSelection);
        tempSelectionCanvas?.drawRect(0f,height/2.toFloat(), width.toFloat(), height.toFloat(),basePaint)
        tempSelectionCanvas?.drawCircle(selectedX.toFloat(), (selectedTab?.centery?: tabList.first().centery).toFloat(),height/3.toFloat(), transparentPaint);
        canvas?.drawBitmap(bitmapSelectionBack, 0f, 0f, noPaint);

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
        val topOffset = (mHeight - 50) / 2
        val leftOffset = (splitWidth - 50) / 2
        var leftPointer = 0

        tabList.forEach {tab->
            tab.centerx = (leftPointer + (leftPointer + splitWidth) )/2
            tab.centery = mHeight/2
            val iconLeft = leftPointer + leftOffset
            tab.startX = leftPointer
            tab.endX = leftPointer + splitWidth


            if(tab == selectedTab) {
                bitmaPaint.alpha = 255
            } else {
                bitmaPaint.alpha = 100
            }

            val bitmap = ContextCompat.getDrawable(context, tab.iconID)!!.toBitmap(50, 50)
            canvas?.drawBitmap(bitmap, iconLeft.toFloat(), topOffset.toFloat(), bitmaPaint)

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
                animateToTab(clickedTab)
                return super.onTouchEvent(event)
            }
        }
        return super.onTouchEvent(event)
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
    var centerx:Int = 0,
    var centery:Int =0,
    var startX:Int = 0,
    var endX:Int = 0
)