package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFFADC6FF),
    onPrimary = Color(0xFF002E69),
    secondary = Color(0xFF43474E),
    onSecondary = Color(0xFFE1E2EC),
    tertiary = Color(0xFF2F3033),
    onTertiary = Color(0xFFD3E2FF),
    background = Color(0xFF111318),
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF111318),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF1B1D23),
    onSurfaceVariant = Color(0xFFC3C6CF),
    outline = Color(0xFF8F909A)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Color(0xFF005AC1),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFE1E2EC),
    onSecondary = Color(0xFF1B1B1F),
    tertiary = Color(0xFFD3E2FF),
    onTertiary = Color(0xFF001D49),
    background = Color(0xFFFDFBFF),
    onBackground = Color(0xFF1B1B1F),
    surface = Color(0xFFFDFBFF),
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFFF3F4F9),
    onSurfaceVariant = Color(0xFF44474E),
    outline = Color(0xFF757780)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
