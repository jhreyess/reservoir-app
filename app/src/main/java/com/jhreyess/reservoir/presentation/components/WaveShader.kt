package com.jhreyess.reservoir.presentation.components

import android.content.res.Configuration
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.jhreyess.reservoir.ui.theme.ReservoirTheme
import org.intellij.lang.annotations.Language

@Language("AGSL")
const val WAVE_SHADER_SRC = """
    float bar(vec2 uv, float start, float height) {
        return step(uv.y, height + start) - step(uv.y, start);
    }
    
    highp uniform float iTime;
    uniform float2 iResolution;
    layout(color) uniform half4 iColor;
    layout(color) uniform half4 iEndColor;
    uniform int iDarkMode;

    float4 main(float2 fragCoord) {
        vec2 uv = fragCoord / iResolution.xy;
        vec3 col = vec3(iColor.rgb);
 
        col * bar(uv, 0., 1.);
        for(float i = 0.1; i < .5; i+= 0.1) {
            float wave = 0.005 * sin((2.75 * uv.x) + (iTime) + (i * 6.));
            uv.y += wave;
            if(iDarkMode == 1) {
                vec3 transition = vec3(0.0, 0.02, 0.1) * bar(uv, i * 10 * 0.02, 1);
                col -= transition;
            } else {
                vec3 transition = vec3(0.1, 0.08, 0.0) * bar(uv, i * 10 * 0.02, 1);
                col += transition;
            }
        }
        
        if(uv.y > 0.08) {
            if(iDarkMode == 1) {
                return vec4(col,1.0) + ((iEndColor - uv.y) * 0.25);
            } else {
                return vec4(col,1.0) + 0.1 + ((iEndColor - uv.y) * 0.25);
            }
        }
        
        return vec4(col, 1.0);
    }
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ShaderPreview () {
    ReservoirTheme {
        val primaryColor = MaterialTheme.colorScheme.primary
        val endColor = if(isSystemInDarkTheme()) {
            Color.Black
        } else {
            Color.Blue
        }
        val isDarkMode = if(isSystemInDarkTheme()) 1 else 0
        Column(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val shader = RuntimeShader(WAVE_SHADER_SRC)
                    val shaderBrush = ShaderBrush(shader)

                    shader.setFloatUniform("iResolution", size.width, size.height)
                    shader.setColorUniform("iColor", primaryColor.toArgb())
                    shader.setColorUniform("iEndColor", endColor.toArgb())
                    shader.setIntUniform("iDarkMode", isDarkMode)
                    onDrawBehind {
                        drawRect(shaderBrush)
                    }
                }
        ) {

        }
    }
}