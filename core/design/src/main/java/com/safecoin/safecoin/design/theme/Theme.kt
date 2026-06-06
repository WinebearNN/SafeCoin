package com.safecoin.safecoin.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.safecoin.safecoin.domain.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueDark,
    onPrimary = SurfaceDark,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = PrimaryBlueDark,
    secondary = AccentTeal,
    onSecondary = SurfaceDark,
    tertiary = IncomeGreen,
    background = SurfaceDark,
    onBackground = Color(0xFFF8FAFC),
    surface = CardDark,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = OnSurfaceMutedDark,
    error = ExpenseRed,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = CardLight,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = PrimaryBlue,
    secondary = AccentTeal,
    onSecondary = CardLight,
    tertiary = IncomeGreen,
    background = SurfaceLight,
    onBackground = Color(0xFF0F172A),
    surface = CardLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = OnSurfaceMutedLight,
    error = ExpenseRed,
)

@Composable
fun SafeCoinTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
