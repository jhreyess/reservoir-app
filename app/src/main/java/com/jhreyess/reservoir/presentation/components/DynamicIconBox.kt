package com.jhreyess.reservoir.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jhreyess.reservoir.presentation.modifyColorHSL
import com.jhreyess.reservoir.ui.theme.ReservoirTheme

@Composable
fun DynamicIconBox(
    imageVector: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    val dynamicTint = if(isSystemInDarkTheme()) {
        modifyColorHSL(tint, -25.0f, .15f ,.12f, true)
    } else tint
    Box(
        modifier = modifier.background(dynamicTint.copy(alpha = 0.25f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = dynamicTint,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DynamicIconBoxPreview() {
    ReservoirTheme {
        Row {
            DynamicIconBox(
                imageVector = Icons.Default.Favorite,
                tint = Color.Red
            )
            DynamicIconBox(
                imageVector = Icons.Default.Favorite,
                tint = Color(0xFF2066FA)
            )
            DynamicIconBox(
                imageVector = Icons.Default.Favorite,
                tint = Color(0xFF0D9D0D)
            )
            DynamicIconBox(
                imageVector = Icons.Default.Favorite,
                tint = Color(0xFFE2D345)
            )
        }
    }
}