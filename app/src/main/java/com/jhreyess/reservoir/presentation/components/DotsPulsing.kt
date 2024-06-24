package com.jhreyess.reservoir.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private const val delayUnit = 300

@Composable
fun DotsPulsing() {
    val infiniteTransition = rememberInfiniteTransition(label = "scaling")

    @Composable
    fun animateScaleWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                0f at delay with LinearEasing
                1f at delay + delayUnit with LinearEasing
                0f at delay + delayUnit * 2
            }
        ), label = "scaling"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(3) {
            val scale by animateScaleWithDelay(delayUnit * it)
            Dot(scale)
        }
    }
}

@Composable
private fun Dot(scale: Float) {
    Spacer(modifier = Modifier
        .size(6.dp)
        .scale(scale)
        .background(
            color = Color.White,
            shape = CircleShape
        )
    )
}