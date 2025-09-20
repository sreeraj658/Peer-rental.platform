package com.ezio.unishare.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color // <<< Ensure this import is definitely included
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Using the colors we defined in Color.kt
private val LightColors = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight
)

// TODO: Define DarkColors based on your app's dark theme design
// For now, it can be a copy of LightColors or a simple dark theme
private val DarkColors = darkColorScheme(
    primary = DeepSkyBlue, // Or a darker variant if desired for dark theme
    onPrimary = White,
    primaryContainer = DodgerBlue, // Or a darker variant
    onPrimaryContainer = White,
    secondary = DeepSkyBlue, // Or a darker variant
    onSecondary = White,
    secondaryContainer = DodgerBlue, // Or a darker variant
    onSecondaryContainer = White,
    tertiary = DeepSkyBlue, // Or a darker variant
    onTertiary = White,
    tertiaryContainer = DodgerBlue, // Or a darker variant
    onTertiaryContainer = White,
    error = ErrorRed,
    onError = White,
    errorContainer = Color(0xFF93000A), // Darker error container
    onErrorContainer = Color(0xFFFFDAD6), // Light text on dark error
    background = Color(0xFF1A1C1E), // Dark background
    onBackground = Color(0xFFE2E2E6), // Light text on dark background
    surface = Color(0xFF1A1C1E), // Dark surface
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF49454F), // Darker surface variant
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99)
)

@Composable
fun PeerRentTheme(
  useDarkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable() () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    useDarkTheme -> DarkColors
    else -> LightColors
  }
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.primary.toArgb() // Or your desired status bar color
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    // You can also define shapes here if needed: shapes = AppShapes,
    content = content
  )
}
