package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val CatLightColorScheme = lightColorScheme(
    primary = CatPeachPrimary,
    onPrimary = Color.White,
    primaryContainer = CatPeachLight,
    onPrimaryContainer = CatBrownText,
    secondary = CatPink,
    onSecondary = Color.White,
    secondaryContainer = CatPinkLight,
    onSecondaryContainer = CatBrownText,
    tertiary = CatMint,
    onTertiary = Color.White,
    tertiaryContainer = CatMintLight,
    onTertiaryContainer = CatBrownText,
    background = CatVanilla,
    onBackground = CatBrownText,
    surface = CatCreamSurface,
    onSurface = CatBrownText,
    surfaceVariant = Color(0xFFFFECE0),
    onSurfaceVariant = CatBrownMuted,
    outline = Color(0xFFFFCCBC),
    outlineVariant = Color(0xFFFFE0B2)
)

private val CatDarkColorScheme = darkColorScheme(
    primary = CatPeachLight,
    onPrimary = CatBrownText,
    primaryContainer = CatPeachDark,
    onPrimaryContainer = Color.White,
    secondary = CatPinkLight,
    onSecondary = CatBrownText,
    secondaryContainer = CatPinkDark,
    onSecondaryContainer = Color.White,
    tertiary = CatMintLight,
    onTertiary = CatBrownText,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = Color(0xFF3E322F),
    onSurfaceVariant = Color(0xFFD7CCC8),
    outline = Color(0xFF5D4037)
)

val CatShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep cheerful custom cat palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> CatDarkColorScheme
        else -> CatLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = CatShapes,
        content = content
    )
}
