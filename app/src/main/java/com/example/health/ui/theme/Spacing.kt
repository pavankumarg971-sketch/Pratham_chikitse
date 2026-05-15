package com.example.health.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,
    val screenPadding: Dp = 16.dp,
    val cardPadding: Dp = 16.dp,
    val sectionSpacing: Dp = 24.dp,
    val itemSpacing: Dp = 12.dp,
    val iconSize: Dp = 24.dp,
    val iconSizeLarge: Dp = 40.dp,
    val buttonHeight: Dp = 48.dp,
    val buttonHeightLarge: Dp = 56.dp,
    val bottomNavHeight: Dp = 64.dp,
    val sosButtonSize: Dp = 64.dp,
    val touchTargetMin: Dp = 48.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }

val Dimens: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current
