package com.jhreyess.reservoir.presentation

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

fun modifyColorHSL(
    color: Color,
    hue: Float,
    saturation: Float,
    lightness: Float,
    debug: Boolean = false
): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(color.toArgb(), hsl)
    val newHue = ((hsl[0] + hue) % 360).coerceIn(0f, 360f)
    val newSaturation = (hsl[1] + saturation).coerceIn(0f, 1f)
    val newLightness = (hsl[2] + lightness).coerceIn(0f, 1f)
    if(debug) {
        Log.d("Colors", newHue.toString())
        Log.d("Colors", newSaturation.toString())
        Log.d("Colors", newLightness.toString())
    }
    return Color.hsl(newHue, newSaturation, newLightness)
}