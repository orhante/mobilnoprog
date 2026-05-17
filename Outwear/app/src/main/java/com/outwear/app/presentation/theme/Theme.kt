package com.outwear.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val OutwearBlack = Color(0xFF0D0D0D)
val OutwearWhite = Color(0xFFFAFAFA)
val OutwearCream = Color(0xFFF5F0EB)
val OutwearTaupe = Color(0xFFC8B8A2)
val OutwearBrown = Color(0xFF6B4E3D)
val OutwearDarkBrown = Color(0xFF3E2723)
val OutwearAccent = Color(0xFFD4A96A)
val OutwearGrey = Color(0xFF9E9E9E)
val OutwearLightGrey = Color(0xFFE8E0D8)
val OutwearError = Color(0xFFB00020)
val OutwearSurface = Color(0xFFF9F5F0)

private val LightColorScheme = lightColorScheme(
    primary = OutwearBrown,
    onPrimary = OutwearWhite,
    primaryContainer = OutwearTaupe,
    onPrimaryContainer = OutwearDarkBrown,
    secondary = OutwearAccent,
    onSecondary = OutwearBlack,
    secondaryContainer = OutwearCream,
    onSecondaryContainer = OutwearDarkBrown,
    background = OutwearSurface,
    onBackground = OutwearBlack,
    surface = OutwearWhite,
    onSurface = OutwearBlack,
    surfaceVariant = OutwearLightGrey,
    onSurfaceVariant = OutwearBrown,
    error = OutwearError,
    outline = OutwearTaupe
)

private val DarkColorScheme = darkColorScheme(
    primary = OutwearTaupe,
    onPrimary = OutwearDarkBrown,
    primaryContainer = OutwearBrown,
    onPrimaryContainer = OutwearCream,
    secondary = OutwearAccent,
    onSecondary = OutwearBlack,
    secondaryContainer = Color(0xFF2C1D14),
    onSecondaryContainer = OutwearAccent,
    background = Color(0xFF1A1210),
    onBackground = OutwearCream,
    surface = Color(0xFF231917),
    onSurface = OutwearCream,
    surfaceVariant = Color(0xFF3E2D26),
    onSurfaceVariant = OutwearTaupe,
    error = Color(0xFFCF6679),
    outline = OutwearBrown
)

@Composable
fun OutwearTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OutwearTypography,
        content = content
    )
}
