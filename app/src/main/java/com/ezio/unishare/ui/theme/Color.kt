package com.ezio.unishare.ui.theme

import androidx.compose.ui.graphics.Color

// From your colors.xml
val DeepSkyBlue = Color(0xFF00BFFF)
val DodgerBlue = Color(0xFF1E90FF)
val White = Color(0xFFFFFFFF)
val ErrorRed = Color(0xFFFF0000)
val Black = Color(0xFF000000)

// You might want to define specific Light and Dark theme colors here.
// For simplicity, we'll start with defining the main ones used.
// Example for Light Theme (can be expanded for Dark Theme)
val PrimaryLight = DeepSkyBlue
val OnPrimaryLight = White
val PrimaryContainerLight = DodgerBlue // Or another lighter/variant shade
val OnPrimaryContainerLight = White

val SecondaryLight = DeepSkyBlue
val OnSecondaryLight = White
val SecondaryContainerLight = DodgerBlue // Or another lighter/variant shade
val OnSecondaryContainerLight = White

val TertiaryLight = DeepSkyBlue // Placeholder, can be a different accent
val OnTertiaryLight = White
val TertiaryContainerLight = DodgerBlue // Placeholder
val OnTertiaryContainerLight = White

val ErrorLight = ErrorRed
val OnErrorLight = White
val ErrorContainerLight = Color(0xFFFFDAD6) // A lighter shade for error container
val OnErrorContainerLight = Color(0xFF410002) // Dark text on light error container

val BackgroundLight = Color(0xFFFDFDFD) // A common light background
val OnBackgroundLight = Color(0xFF1A1C1E) // Dark text on light background

val SurfaceLight = Color(0xFFFDFDFD) // Can be same as background or slightly different
val OnSurfaceLight = Color(0xFF1A1C1E)
val SurfaceVariantLight = Color(0xFFE7E0EC) // For card backgrounds, etc.
val OnSurfaceVariantLight = Color(0xFF49454F)

val OutlineLight = Color(0xFF79747E) // For borders, dividers

// TODO: Define corresponding colors for Dark Theme
// val PrimaryDark = ...
// val OnPrimaryDark = ...
// ... and so on for all roles.