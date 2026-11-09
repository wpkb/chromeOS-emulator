package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = ChromeOSBlueDark,
    onPrimary = Color.Black,
    primaryContainer = ChromeOSBlue,
    onPrimaryContainer = Color.White,
    secondary = ChromeOSBlueDark,
    onSecondary = Color.Black,
    surface = ChromeOSSurfaceDark,
    onSurface = ChromeOSTextPrimaryDark,
    surfaceVariant = ChromeOSSurfaceVariantDark,
    onSurfaceVariant = ChromeOSTextSecondaryDark,
    background = ChromeOSDarkBg,
    onBackground = ChromeOSTextPrimaryDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ChromeOSBlue,
    onPrimary = Color.White,
    primaryContainer = ChromeOSBlueDark,
    onPrimaryContainer = Color.Black,
    secondary = ChromeOSBlue,
    onSecondary = Color.White,
    surface = ChromeOSSurfaceLight,
    onSurface = ChromeOSTextPrimaryLight,
    surfaceVariant = ChromeOSSurfaceVariantLight,
    onSurfaceVariant = ChromeOSTextSecondaryLight,
    background = ChromeOSLightBg,
    onBackground = ChromeOSTextPrimaryLight,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to iconic ChromeOS dark UI
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
