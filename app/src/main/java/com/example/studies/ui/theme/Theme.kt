package com.example.studies.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6B6969),
    secondary = Color(0xFFCDCDCD),
    tertiary = Color(0xFF424242)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6B6969),
    secondary = Color(0xFFCDCDCD),
    tertiary = Color(0xFF424242),
    background = Color.Transparent,
    surface = Color.Transparent
)

@Composable
fun StudiesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xFFCDCDCD),
                        0.5f to Color(0xFFCDCDCD),
                        1.0f to Color(0xFFC4C4C4)
                    )
                )
            )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}