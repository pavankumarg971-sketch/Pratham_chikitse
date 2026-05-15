package com.example.health.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MedicalTeal,
    onPrimary = Color.White,
    primaryContainer = MedicalTealContainer,
    onPrimaryContainer = MedicalTealDark,
    secondary = DeepBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6E4F0),
    onSecondaryContainer = DeepBlueDark,
    tertiary = SafeGreen,
    onTertiary = Color.White,
    tertiaryContainer = SafeGreenContainer,
    onTertiaryContainer = Color(0xFF002200),
    error = EmergencyRed,
    onError = Color.White,
    errorContainer = EmergencyRedContainer,
    onErrorContainer = EmergencyRedDark,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceContainerLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF5F6F8),
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = Color(0xFFDFE2E7)
)

private val DarkColorScheme = darkColorScheme(
    primary = MedicalTealLight,
    onPrimary = MedicalTealDark,
    primaryContainer = MedicalTealContainerDark,
    onPrimaryContainer = MedicalTealContainer,
    secondary = DeepBlueLight,
    onSecondary = DeepBlueDark,
    secondaryContainer = Color(0xFF1E3A5F),
    onSecondaryContainer = Color(0xFFD6E4F0),
    tertiary = SafeGreenLight,
    onTertiary = Color(0xFF003300),
    tertiaryContainer = SafeGreenContainerDark,
    onTertiaryContainer = SafeGreenContainer,
    error = EmergencyRedLight,
    onError = EmergencyRedDark,
    errorContainer = EmergencyRedContainerDark,
    onErrorContainer = EmergencyRedContainer,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceContainerDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    surfaceContainerLowest = Color(0xFF0A0E13),
    surfaceContainerLow = Color(0xFF141920),
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = Color(0xFF303740)
)

@Immutable
data class ExtendedColors(
    val emergency: Color = EmergencyRed,
    val emergencyContainer: Color = EmergencyRedContainer,
    val onEmergency: Color = Color.White,
    val warning: Color = WarningAmber,
    val warningContainer: Color = WarningAmberContainer,
    val onWarning: Color = Color.White,
    val safe: Color = SafeGreen,
    val safeContainer: Color = SafeGreenContainer,
    val onSafe: Color = Color.White,
    val severityCritical: Color = SeverityCritical,
    val severityHigh: Color = SeverityHigh,
    val severityMedium: Color = SeverityMedium,
    val severityLow: Color = SeverityLow
)

private val LightExtendedColors = ExtendedColors()

private val DarkExtendedColors = ExtendedColors(
    emergency = EmergencyRedLight,
    emergencyContainer = EmergencyRedContainerDark,
    onEmergency = Color.White,
    warning = WarningAmberLight,
    warningContainer = WarningAmberContainerDark,
    onWarning = Color.Black,
    safe = SafeGreenLight,
    safeContainer = SafeGreenContainerDark,
    onSafe = Color.Black,
    severityCritical = EmergencyRedLight,
    severityHigh = Color(0xFFFF8A65),
    severityMedium = WarningAmberLight,
    severityLow = SafeGreenLight
)

val LocalExtendedColors = staticCompositionLocalOf { ExtendedColors() }

@Composable
fun HealthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

object HealthThemeExtras {
    val colors: ExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current
}