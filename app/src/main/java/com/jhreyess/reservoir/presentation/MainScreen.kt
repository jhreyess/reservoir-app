package com.jhreyess.reservoir.presentation

import android.content.res.Configuration
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhreyess.reservoir.R
import com.jhreyess.reservoir.data.local.DamEntity
import com.jhreyess.reservoir.data.local.RecordEntity
import com.jhreyess.reservoir.presentation.components.AnimatedCircle
import com.jhreyess.reservoir.presentation.components.BarGraph
import com.jhreyess.reservoir.presentation.components.DotsPulsing
import com.jhreyess.reservoir.presentation.components.DynamicIconBox
import com.jhreyess.reservoir.presentation.components.WAVE_SHADER_SRC
import com.jhreyess.reservoir.presentation.components.WaterLevelIndicator
import com.jhreyess.reservoir.ui.theme.ReservoirTheme
import com.jhreyess.reservoir.util.asTimestamp
import com.jhreyess.reservoir.util.formatDecimals
import com.jhreyess.reservoir.util.getCurrentDate
import com.jhreyess.reservoir.util.relativeTime
import kotlinx.coroutines.delay
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainScreen(
    state: HomeState,
    onEvent: (ScreenEvent) -> Unit,
    onInfoClick: () -> Unit
) {

    var showUpdateTime by remember { mutableStateOf(false) }
    LaunchedEffect(state.isLoading) {
        if(!state.isLoading) {
            showUpdateTime = true
            delay(3000)
            showUpdateTime = false
        } else {
            showUpdateTime = false
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Nuevo León",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        AnimatedVisibility(showUpdateTime) {
                            Text(
                                text = "Actualizado hace ${state.lastUpdate.relativeTime()}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        AnimatedVisibility(state.isLoading) {
                            Spacer(modifier = Modifier.height(4.dp))
                            DotsPulsing()
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onEvent(ScreenEvent.Refresh) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refrescar",
                            tint = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onInfoClick
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Información",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = primaryColor
                )
            )
        }
    ) { paddingValues ->
        val isDarkMode = if(isSystemInDarkTheme()) 1 else 0
        val endColor = if(isDarkMode == 1) {
            Color.Black
        } else {
            Color.Blue
        }
        val time by produceState(0f) {
            while(true) {
                withInfiniteAnimationFrameMillis {
                    value = it / 1000f
                }
            }
        }
        Column(
            Modifier
                .consumeWindowInsets(paddingValues)
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize()
                .then(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Modifier.drawWithCache {
                            val shader = RuntimeShader(WAVE_SHADER_SRC)
                            val shaderBrush = ShaderBrush(shader)

                            shader.setFloatUniform("iResolution", size.width, size.height)
                            shader.setColorUniform("iColor", primaryColor.toArgb())
                            shader.setColorUniform("iEndColor", endColor.toArgb())
                            shader.setIntUniform("iDarkMode", isDarkMode)
                            onDrawBehind {
                                shader.setFloatUniform("iTime", time)
                                drawRect(shaderBrush)
                            }
                        }
                    } else {
                        val colorList: List<Color> = when (isDarkMode) {
                            1 -> listOf(Color(0xFF001c7e), Color.Black)
                            else -> listOf(Color(0xFFccd6fe), Color(0xFF325FFF))
                        }
                        Modifier.background(
                            brush = Brush.verticalGradient(colorList)
                        )
                    }
                )
        ) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                val resource = if(isDarkMode == 1) R.drawable.ic_wave_dark else R.drawable.ic_wave_light
                Image(
                    painter = painterResource(id = resource),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillBounds
                )
            } else {
                Spacer(Modifier.height(60.dp))
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = paddingValues.calculateBottomPadding(),
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                    )
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            ) {
                DamLevels(state.dams)
                Spacer(Modifier.height(24.dp))
                AdditionalInformation(
                    totalStorage = state.totalStorage,
                    currentStorage = state.currentStorage,
                    diffPercentage = state.diff,
                    diffValue = state.diffValue,
                    dams = state.dams
                )
                Spacer(Modifier.height(8.dp))
                BarGraph(
                    data = state.records,
                    total = state.totalStorage.toInt(),
                    /** TODO: build a new layout for landscape mode **/
                )
            }
        }
    }
}

@Composable
fun DamLevels(
    dams: List<DamEntity>,
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = "Niveles de las presas",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val circleModifier = Modifier.size(110.dp)
            val textFontSize = 16.sp
            val labelFontSize = 10.sp
            dams.forEach {
                val strokeColor = when(it.currentPercStorage) {
                    in 0.0f..0.33f -> Color(0xFFA30000)
                    in 0.33f..0.66f -> Color(0xFFD16F00)
                    in 0.66..1.0 -> Color(0xFF0D9D0D)
                    else -> Color(0xFF0D9D0D)
                }
                AnimatedCircle(
                    percentage = it.currentPercStorage,
                    strokeColor = strokeColor,
                    label = it.name,
                    modifier = circleModifier,
                    textFontSize = textFontSize,
                    labelFontSize = labelFontSize,
                    strokeWidth = 5.dp,
                )
            }
        }
    }
}

@Composable
fun AdditionalInformation(
    totalStorage: Float,
    currentStorage: Float,
    diffPercentage: Float,
    diffValue: Float,
    dams: List<DamEntity>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 168.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DataCard(
                title = "Niveles de agua actuales",
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DynamicIconBox(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_water_drop),
                        tint = Color(0xFF2066FA),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    WaterLevelIndicator(
                        value = currentStorage,
                        total = totalStorage,
                        strokeColor = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            DataCard(
                title = "Diferencia en los últimos 5 días",
                modifier = Modifier.weight(1f)
            ) {

                val (icon, secondaryIcon, color) = when {
                    diffPercentage > 0 -> Triple(
                        ImageVector.vectorResource(id = R.drawable.ic_trending_up),
                        Icons.Default.KeyboardArrowUp,
                        Color(0xFF0D9D0D)
                    )
                    diffPercentage < 0 -> Triple(
                        ImageVector.vectorResource(id = R.drawable.ic_trending_down),
                        Icons.Default.KeyboardArrowDown,
                        Color(0xFFA30000)
                    )
                    else -> Triple(
                        ImageVector.vectorResource(id = R.drawable.ic_trending_flat),
                        Icons.Default.KeyboardArrowRight,
                        Color(0xFF979797)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DynamicIconBox(
                        imageVector = icon,
                        tint = color,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${diffPercentage.times(100).formatDecimals()}%",
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Icon(
                                imageVector = secondaryIcon,
                                contentDescription = null,
                                tint = color
                            )
                        }
                        Text(
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            text = buildAnnotatedString {
                                append("Equivalente a ${diffValue.formatDecimals()} hm")
                                withStyle(
                                    style = SpanStyle(
                                        fontSize = 7.sp,
                                        baselineShift = BaselineShift.Superscript
                                    )
                                ) {
                                    append("3")
                                }
                            }
                        )
                    }
                }
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            DataCard(
                title = "Niveles de agua Máximas Ordinarias (NAMO)",
                modifier = Modifier.fillMaxSize(),
                maxLines = 2
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    dams.forEach {
                        WaterLevelIndicator(
                            label = it.name,
                            value = it.currentStorage,
                            total = it.totalStorage,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DataCard(
    title: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = maxLines,
                overflow = TextOverflow.Visible,
                lineHeight = 12.sp,
                modifier = Modifier.fillMaxWidth()
            )
            content()
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, apiLevel = Build.VERSION_CODES.S)
@Preview(apiLevel = Build.VERSION_CODES.S)
@Composable
fun AppPreview() {
    ReservoirTheme {
        val presas = listOf(
            DamEntity("LB","La boca", "",39.5f,15.13f, 0.3832f,""),
            DamEntity("C","El Cuchillo", "",1123.14f,1025.25f, 0.3608f,""),
            DamEntity("CP","Cerro Prieto", "",300f,21.7f, 0.0723f,""),
        )
        val state = HomeState(
            dams = presas,
            diff = 0.1523f,
            currentStorage = 442.08f,
            totalStorage = 1462.6f,
            records = List(10) { i -> RecordEntity(500f, Date(getCurrentDate(-i).asTimestamp())) },
            lastUpdate = 0L,
            isLoading = false,
        )
        MainScreen(state, {}, {})
    }
}