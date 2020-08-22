package com.pebbles.ui.Custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.pebbles.R
import com.scwang.wave.MultiWaveHeader

class WaterLevelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    lateinit var waterLevelView: MultiWaveHeader


init {
    val view = LayoutInflater.from(context).inflate(R.layout.water_level_view, this, true)
    waterLevelView = view.findViewById(R.id.waterView)
    waterLevelView.velocity = 3f
    waterLevelView.progress = 1f
    waterLevelView.isRunning()
    waterLevelView.gradientAngle = 45
    waterLevelView.waveHeight = 20
    waterLevelView.startColor = Color.BLUE
    waterLevelView.closeColor = Color.CYAN

}

}