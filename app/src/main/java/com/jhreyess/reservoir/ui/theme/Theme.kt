package com.jhreyess.reservoir.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Blue,
    secondary = Rose,
    tertiary = Aqua,

    surface = CardBackground,
    onSurface = Snow,
    onSurfaceVariant = PaleBlue,
    onBackground = Gray,

    primaryContainer = Gold,

    outline = DarkGray,
)

private val LightColorScheme = lightColorScheme(
    primary = LightBlue,
    secondary = Lavender,
    tertiary = Sea,

    surface = LightCardBackground,
    onSurface = NavyBlue,
    onSurfaceVariant = Water,
    onBackground = DarkGrayishBlue,

    primaryContainer = Coin,

    outline = GrayishBlue
)

@Composable
fun ReservoirTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        /*dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }*/

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}