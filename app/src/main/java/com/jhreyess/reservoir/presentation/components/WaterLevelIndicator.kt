package com.jhreyess.reservoir.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhreyess.reservoir.ui.theme.ReservoirTheme
import com.jhreyess.reservoir.util.formatDecimals

@Composable
fun WaterLevelIndicator(
    modifier: Modifier = Modifier,
    label: String? = null,
    value: Float = 0.0f,
    total: Float = 1.0f,
    strokeColor: Color = MaterialTheme.colorScheme.tertiary
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            label?.let {
                Text(
                    modifier = Modifier.alignByBaseline().fillMaxWidth(0.4f),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 7.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = it.uppercase()
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.alignByBaseline(),
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                    ){
                        append(value.formatDecimals())
                    }
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 9.sp
                        )
                    ) {
                        append(" / ")
                        append(total.formatDecimals())
                        append("hm")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 7.sp,
                            baselineShift = BaselineShift.Superscript
                        )
                    ) {
                        append("3")
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = value / total,
            modifier = Modifier.fillMaxWidth(),
            color = strokeColor,
            trackColor = MaterialTheme.colorScheme.outline,
            strokeCap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WaterLevelIndicatorPreview() {
    ReservoirTheme {
        Column {
            WaterLevelIndicator(
                modifier = Modifier.widthIn(max = 250.dp).padding(8.dp),
                label = "testing",
                value = 1140.99f,
                total = 1148.60f
            )
        }
    }
}