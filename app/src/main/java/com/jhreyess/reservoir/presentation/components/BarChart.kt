package com.jhreyess.reservoir.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhreyess.reservoir.data.local.RecordEntity
import com.jhreyess.reservoir.presentation.DataCard
import com.jhreyess.reservoir.ui.theme.ReservoirTheme
import com.jhreyess.reservoir.util.formatDecimals
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BarGraph(
    data: List<RecordEntity>,
    total: Int,
    modifier: Modifier = Modifier
) {
    var selectedIndex: Long by remember { mutableLongStateOf(-1L) }
    var details: Float by remember { mutableFloatStateOf(0.0f) }
    val reversedRecords = remember(data) { data.reversed() }

    DataCard(
        title = "Almacenamiento en 30 días",
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if(selectedIndex != -1L) {
                InfoChip(
                    info = details.formatDecimals(),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = (-8).dp)
                )
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                stickyHeader {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.weight(1f)
                        ) {
                            val steps = 2
                            val decrement = total / (steps + 1)
                            repeat(times = steps + 2) { idx ->
                                val sequence = total - decrement * idx
                                Text(
                                    text = buildAnnotatedString {
                                        append(sequence.toString())
                                        append(" hm")
                                        withStyle(
                                            style = SpanStyle(
                                                fontSize = 7.sp,
                                                baselineShift = BaselineShift.Superscript
                                            )
                                        ) {
                                            append("3")
                                        }
                                    },
                                    fontSize = 10.sp,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }

                items(
                    items = reversedRecords,
                ) { record ->
                    val fraction = record.waterLevels / total
                    val id = record.timestamp.time

                    val isSelected by remember {
                        derivedStateOf { selectedIndex == id }
                    }
                    Bar(
                        fraction = fraction,
                        date = record.timestamp,
                        selected = isSelected,
                        onSelected = {
                            selectedIndex = if (selectedIndex != id) id else -1L
                            if(selectedIndex != -1L) details = record.waterLevels
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Bar(
    fraction: Float,
    date: Date,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Column(
        modifier = Modifier
            .widthIn(min = 40.dp)
            .width(IntrinsicSize.Max)
            .fillMaxHeight()
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 5.dp)
                    .fillMaxHeight(fraction)
                    .then(
                        if (selected)
                            Modifier.background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(4.dp)
                            )
                        else
                            Modifier.background(
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                RoundedCornerShape(4.dp)
                            )
                    )
                    .clickable { onSelected() }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        val dateFormat = SimpleDateFormat("E\nd/M", Locale.getDefault())
        val formattedData = dateFormat.format(date)
        Text(
            text = formattedData.toString().replaceFirstChar { it.uppercase() },
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun InfoChip(
    info: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(vertical = 4.dp, horizontal = 6.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                append(info)
                append(" hm")
                withStyle(
                    style = SpanStyle(
                        fontSize = 7.sp,
                        baselineShift = BaselineShift.Superscript
                    )
                ) {
                    append("3")
                }
            },
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun BarChartPreview() {
    val calendar = Calendar.getInstance()
    ReservoirTheme {
        val data = List(10) {
            RecordEntity(
                Math.random().toFloat() * 1000,
                calendar.apply { add(Calendar.DAY_OF_YEAR, -1) }.time
            )
        }
        BarGraph(data, 1500)
    }
}