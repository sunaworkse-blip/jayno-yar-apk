package com.example.jaynoyar.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

val LocalJaynoTheme = staticCompositionLocalOf<JaynoCustomTheme> {
    TwelveThemes.getTheme(AppThemeKey.WORKSHOP_WOOD)
}

@Composable
fun JaynoYarTheme(
    themeKey: AppThemeKey = AppThemeKey.WORKSHOP_WOOD,
    content: @Composable () -> Unit
) {
    val customTheme = TwelveThemes.getTheme(themeKey)

    val m3ColorScheme: ColorScheme = if (customTheme.isDark) {
        darkColorScheme(
            primary = customTheme.primary,
            onPrimary = customTheme.onPrimary,
            primaryContainer = customTheme.primaryContainer,
            onPrimaryContainer = customTheme.onPrimaryContainer,
            secondary = customTheme.secondary,
            onSecondary = customTheme.onSecondary,
            background = customTheme.background,
            onBackground = customTheme.onBackground,
            surface = customTheme.surface,
            onSurface = customTheme.onSurface,
            surfaceVariant = customTheme.surfaceVariant,
            onSurfaceVariant = customTheme.onSurfaceVariant,
            error = customTheme.errorColor
        )
    } else {
        lightColorScheme(
            primary = customTheme.primary,
            onPrimary = customTheme.onPrimary,
            primaryContainer = customTheme.primaryContainer,
            onPrimaryContainer = customTheme.onPrimaryContainer,
            secondary = customTheme.secondary,
            onSecondary = customTheme.onSecondary,
            background = customTheme.background,
            onBackground = customTheme.onBackground,
            surface = customTheme.surface,
            onSurface = customTheme.onSurface,
            surfaceVariant = customTheme.surfaceVariant,
            onSurfaceVariant = customTheme.onSurfaceVariant,
            error = customTheme.errorColor
        )
    }

    CompositionLocalProvider(
        LocalJaynoTheme provides customTheme,
        LocalLayoutDirection provides LayoutDirection.Rtl // Full Persian RTL interface
    ) {
        MaterialTheme(
            colorScheme = m3ColorScheme,
            content = content
        )
    }
}
