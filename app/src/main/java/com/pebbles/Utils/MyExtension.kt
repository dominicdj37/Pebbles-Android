package com.pebbles.Utils


fun Int.mapToValues(inMin: Float, inMax: Float, outMin: Float, outMax: Float):Float {
    val inSpan = inMax - inMin
    val outSpan = outMax - outMin
    val scaleFactor = outSpan / inSpan
    return (15 + (this) * scaleFactor)
}