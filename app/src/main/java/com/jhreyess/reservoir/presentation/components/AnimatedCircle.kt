package com.jhreyess.reservoir.presentation.components

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhreyess.reservoir.presentation.modifyColorHSL
import com.jhreyess.reservoir.ui.theme.ReservoirTheme

@Composable
fun AnimatedCircle(
    percentage: Float,
    strokeColor: Color,
    label: String,
    modifier: Modifier = Modifier,
    outlineColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
    textFontSize: TextUnit = 20.sp,
    labelFontSize: TextUnit = 14.sp,
    strokeWidth: Dp = 8.dp,
    initialValue: Float = 0.0f,
) {
    val stroke = with(LocalDensity.current) {
        Stroke(
            width = strokeWidth.toPx(),
            cap = StrokeCap.Round
        )
    }
    val animatedPercentage = remember { Animatable(initialValue) }
    LaunchedEffect(percentage) {
        Log.d("Effect", "Launched...")
        animatedPercentage.animateTo(
            targetValue = percentage,
            animationSpec = tween(
                durationMillis = 1500
            )
        )
    }

    val textStyle: TextStyle = TextStyle.Default.copy(
        fontSize = textFontSize,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
    )
    val labelStyle: TextStyle = TextStyle.Default.copy(
        fontSize = labelFontSize,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
    )

    val textMeasurer = rememberTextMeasurer()

    val dynamicStrokeColor = if(isSystemInDarkTheme())
        modifyColorHSL(strokeColor, 15f, 0f, 0.16f)
    else strokeColor
    Canvas(
        modifier = modifier
            .drawWithCache {
                val textResult =
                    textMeasurer.measure(
                        text = AnnotatedString("%.2f%%\n".format(animatedPercentage.value * 100)),
                        style = textStyle
                    )
                val labelResult =
                    textMeasurer.measure(
                        text = AnnotatedString("\n${label.uppercase()}"),
                        style = labelStyle
                    )
                onDrawBehind {
                    drawText(
                        textLayoutResult = textResult,
                        topLeft = Offset(
                            (size.width - textResult.size.width) * 0.5f + 5,
                            (size.height - textResult.size.height) * 0.5f
                        )
                    )
                    drawText(
                        textLayoutResult = labelResult,
                        topLeft = Offset(
                            (size.width - labelResult.size.width) * 0.5f,
                            (size.height - labelResult.size.height) * 0.5f + 5
                        )
                    )
                }
            }
            .size(200.dp)
    ) {
        val innerRadius = (size.minDimension - stroke.width) * 0.5f
        val halfSize = size * 0.5f
        val topLeft = Offset(
            halfSize.width - innerRadius,
            halfSize.height - innerRadius
        )
        val innerSize = Size(innerRadius * 2, innerRadius * 2)
        drawArc(
            color = outlineColor,
            startAngle = 0f,
            sweepAngle = 360f,
            topLeft = topLeft,
            size = innerSize,
            useCenter = false,
            style = stroke
        )
        drawArc(
            color = dynamicStrokeColor,
            startAngle = 0f,
            sweepAngle = animatedPercentage.value * 360f,
            topLeft = topLeft,
            size = innerSize,
            useCenter = false,
            style = stroke
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, backgroundColor = 0xFF001982, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AnimatedCirclePreview() {
    ReservoirTheme {
        AnimatedCircle(percentage = 1f, strokeColor = Color(0xFFD16F00), label = "Testing")
    }
}
