package com.pebbles.ui.Custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import com.bumptech.glide.load.model.FileLoader
import com.pebbles.R
import com.pebbles.Utils.mapToValues
import com.scwang.wave.MultiWaveHeader

class WaterLevelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var waterLevelView: MultiWaveHeader
    private var waterLevelGuideLine: Guideline

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.water_level_view, this, true)
        waterLevelView = view.findViewById(R.id.waterView)
        waterLevelGuideLine = view.findViewById(R.id.waterLevelGuideLine)

        setupWaterLevel()
        setWaterLevel(75)
    }

    fun setWaterLevel(percentage: Int) {
        val inversePercentage = 100 - percentage
        val level = inversePercentage.mapToValues(0f,100f, 15f, 80f) / 100f

        val animator = ValueAnimator.ofFloat(.80f, level)
        animator.addUpdateListener {
            waterLevelGuideLine.setGuidelinePercent(it.animatedValue as Float)
        }
        animator.interpolator = DecelerateInterpolator()
        animator.duration = 2000
        animator.start()


    }

    private fun setupWaterLevel() {
        waterLevelView.velocity = 3f
        waterLevelView.progress = 1f
        waterLevelView.gradientAngle = 45
        waterLevelView.waveHeight = 20
        waterLevelView.startColor = Color.BLUE
        waterLevelView.closeColor = Color.CYAN
    }
}
