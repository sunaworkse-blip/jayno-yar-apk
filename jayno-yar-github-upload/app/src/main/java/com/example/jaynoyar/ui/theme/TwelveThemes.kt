package com.example.jaynoyar.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object TwelveThemes {

    fun getTheme(key: AppThemeKey): JaynoCustomTheme {
        return when (key) {
            AppThemeKey.MINIMAL_LIGHT -> JaynoCustomTheme(
                key = key,
                isDark = false,
                primary = Color(0xFF1E293B),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFE2E8F0),
                onPrimaryContainer = Color(0xFF0F172A),
                secondary = Color(0xFF475569),
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFFF8FAFC),
                onBackground = Color(0xFF0F172A),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF0F172A),
                surfaceVariant = Color(0xFFF1F5F9),
                onSurfaceVariant = Color(0xFF334155),
                cardBackground = Color(0xFFFFFFFF),
                cardBorderColor = Color(0xFFE2E8F0),
                cardBorderWidth = 1.dp,
                cardShape = RoundedCornerShape(12.dp),
                cardElevation = 1.dp,
                backgroundBrush = Brush.verticalGradient(
                    listOf(Color(0xFFF8FAFC), Color(0xFFF1F5F9))
                ),
                accentPillColor = Color(0xFF0284C7),
                successColor = Color(0xFF059669),
                warningColor = Color(0xFFD97706),
                errorColor = Color(0xFFDC2626),
                infoColor = Color(0xFF2563EB)
            )

            AppThemeKey.PREMIUM_DARK -> JaynoCustomTheme(
                key = key,
                isDark = true,
                primary = Color(0xFFF59E0B),
                onPrimary = Color(0xFF1F1203),
                primaryContainer = Color(0xFF3B2306),
                onPrimaryContainer = Color(0xFFFCD34D),
                secondary = Color(0xFFD97706),
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFF0B0F19),
                onBackground = Color(0xFFF1F5F9),
                surface = Color(0xFF111827),
                onSurface = Color(0xFFF9FAFB),
                surfaceVariant = Color(0xFF1F2937),
                onSurfaceVariant = Color(0xFF9CA3AF),
                cardBackground = Color(0xFF131C2E),
                cardBorderColor = Color(0x3DF59E0B),
                cardBorderWidth = 1.dp,
                cardShape = RoundedCornerShape(16.dp),
                cardElevation = 3.dp,
                backgroundBrush = Brush.verticalGradient(
                    listOf(Color(0xFF0B0F19), Color(0xFF111827))
                ),
                accentPillColor = Color(0xFFF59E0B),
                successColor = Color(0xFF10B981),
                warningColor = Color(0xFFFBBF24),
                errorColor = Color(0xFFF87171),
                infoColor = Color(0xFF60A5FA)
            )

            AppThemeKey.WORKSHOP_WOOD -> JaynoCustomTheme(
                key = key,
                isDark = true,
                primary = Color(0xFFD97706), // warm oak amber
                onPrimary = Color(0xFF291404),
                primaryContainer = Color(0xFF451A03), // dark walnut
                onPrimaryContainer = Color(0xFFFDE68A),
                secondary = Color(0xFFB45309), // timber
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFF1C130D), // deep smoked wood
                onBackground = Color(0xFFFDF6EC),
                surface = Color(0xFF2A1C14),
                onSurface = Color(0xFFFDF6EC),
                surfaceVariant = Color(0xFF38261B),
                onSurfaceVariant = Color(0xFFD6C7B2),
                cardBackground = Color(0xFF261911),
                cardBorderColor = Color(0x40D97706),
                cardBorderWidth = 1.2.dp,
                cardShape = RoundedCornerShape(14.dp),
                cardElevation = 2.dp,
                backgroundBrush = Brush.radialGradient(
                    listOf(Color(0xFF2E1D13), Color(0xFF170F0A))
                ),
                accentPillColor = Color(0xFFF59E0B),
                successColor = Color(0xFF15803D),
                warningColor = Color(0xFFEAB308),
                errorColor = Color(0xFFEF4444),
                infoColor = Color(0xFF38BDF8)
            )

            AppThemeKey.GLASSMORPHISM -> JaynoCustomTheme(
                key = key,
                isDark = true,
                primary = Color(0xFF818CF8),
                onPrimary = Color(0xFF0F172A),
                primaryContainer = Color(0xFF312E81),
                onPrimaryContainer = Color(0xFFC7D2FE),
                secondary = Color(0xFFA855F7),
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFF0F172A),
                onBackground = Color(0xFFF8FAFC),
                surface = Color(0x331E293B),
                onSurface = Color(0xFFF8FAFC),
                surfaceVariant = Color(0x4D334155),
                onSurfaceVariant = Color(0xFFCBD5E1),
                cardBackground = Color(0x401E293B),
                cardBorderColor = Color(0x40818CF8),
                cardBorderWidth = 1.5.dp,
                cardShape = RoundedCornerShape(20.dp),
                cardElevation = 4.dp,
                backgroundBrush = Brush.verticalGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF1E1B4B))
                ),
                accentPillColor = Color(0xFFA855F7),
                successColor = Color(0xFF34D399),
                warningColor = Color(0xFFFBBF24),
                errorColor = Color(0xFFF87171),
                infoColor = Color(0xFF38BDF8)
            )

            AppThemeKey.ENTERPRISE_NAVY -> JaynoCustomTheme(
                key = key,
                isDark = true,
                primary = Color(0xFF3B82F6),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFF1E3A8A),
                onPrimaryContainer = Color(0xFFDBEAFE),
                secondary = Color(0xFF60A5FA),
                onSecondary = Color(0xFF0F172A),
                background = Color(0xFF0A0F1D),
                onBackground = Color(0xFFF1F5F9),
                surface = Color(0xFF0F172A),
                onSurface = Color(0xFFF8FAFC),
                surfaceVariant = Color(0xFF1E293B),
                onSurfaceVariant = Color(0xFF94A3B8),
                cardBackground = Color(0xFF131D31),
                cardBorderColor = Color(0xFF1E293B),
                cardBorderWidth = 1.dp,
                cardShape = RoundedCornerShape(8.dp),
                cardElevation = 2.dp,
                backgroundBrush = Brush.verticalGradient(
                    listOf(Color(0xFF0A0F1D), Color(0xFF0F172A))
                ),
                accentPillColor = Color(0xFF3B82F6),
                successColor = Color(0xFF10B981),
                warningColor = Color(0xFFF59E0B),
                errorColor = Color(0xFFEF4444),
                infoColor = Color(0xFF60A5FA)
            )

            AppThemeKey.SOFT_NEUTRAL -> JaynoCustomTheme(
                key = key,
                isDark = false,
                primary = Color(0xFFB45309), // terracotta
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFFEF3C7),
                onPrimaryContainer = Color(0xFF78350F),
                secondary = Color(0xFF78716C),
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFFF9F7F1), // linen parchment
                onBackground = Color(0xFF292524),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF292524),
                surfaceVariant = Color(0xFFF0EDE4),
                onSurfaceVariant = Color(0xFF57534E),
                cardBackground = Color(0xFFFFFFFF),
                cardBorderColor = Color(0xFFE7E2D6),
                cardBorderWidth = 1.dp,
                cardShape = RoundedCornerShape(16.dp),
                cardElevation = 1.dp,
                backgroundBrush = Brush.verticalGradient(
                    listOf(Color(0xFFFBF9F5), Color(0xFFF0EDE4))
                ),
                accentPillColor = Color(0xFFB45309),
                successColor = Color(0xFF15803D),
                warningColor = Color(0xFFD97706),
                errorColor = Color(0xFFDC2626),
                infoColor = Color(0xFF0369A1)
            )

            AppThemeKey.DEEP_SAPPHIRE -> JaynoCustomTheme(
                key = key,
                isDark = true,
                primary = Color(0xFF06B6D4), // electric cyan
                onPrimary = Color(0xFF083344),
                primaryContainer = Color(0xFF164E63),
                onPrimaryContainer = Color(0xFFA5F3FC),
                secondary = Color(0xFF38BDF8),
                onSecondary = Color(0xFF0C4A6E),
                background = Color(0xFF051329), // deep Persian lapis
                onBackground = Color(0xFFF0FDF4),
                surface = Color(0xFF0B1E3C),
                onSurface = Color(0xFFF0F9FF),
                surfaceVariant = Color(0xFF132B52),
                onSurfaceVariant = Color(0xFFBAE6FD),
                cardBackground = Color(0xFF0E2448),
                cardBorderColor = Color(0x4006B6D4),
                cardBorderWidth = 1.2.dp,
                cardShape = RoundedCornerShape(14.dp),
                cardElevation = 3.dp,
                backgroundBrush = Brush.verticalGradient(
                    listOf(Color(0xFF051329), Color(0xFF0B2247))
                ),
                accentPillColor = Color(0xFF06B6D4),
                successColor = Color(0xFF10B981),
                warningColor = Color(0xFFF59E0B),
                errorColor = Color(0xFFF87171),
                infoColor = Color(0xFF38BDF8)
            )

            AppThemeKey.FIROUZEH_EMERALD -> JaynoCustomTheme(
                key = key,
                isDark = false,
                primary = Color(0xFF0F766E), // Persian Firouzeh
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFCCFBF1),
                onPrimaryContainer = Color(0xFF115E59),
                secondary = Color(0xFF059669), // Emerald
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFFF2FBF9),
                onBackground = Color(0xFF134E4A),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF134E4A),
                surfaceVariant = Color(0xFFE6F7F4),
                onSurfaceVariant = Color(0xFF115E59),
                cardBackground = Color(0xFFFFFFFF),
                cardBorderColor = Color(0xFF99F6E4),
                cardBorderWidth = 1.dp,
                cardShape = RoundedCornerShape(14.dp),
                cardElevation = 2.dp,
                backgroundBrush = Brush.verticalGradient(
                    listOf(Color(0xFFF2FBF9), Color(0xFFE0F2FE))
                ),
                accentPillColor = Color(0xFF0D9488),
                successColor = Color(0xFF059669),
                warningColor = Color(0xFFD97706),
                errorColor = Color(0xFFE11D48),
                infoColor = Color(0xFF0284C7)
            )

            AppThemeKey.ELEGANTE_MONOCHROME -> JaynoCustomTheme(
                key = key,
                isDark = true,
                primary = Color(0xFFFFFFFF),
                onPrimary = Color(0xFF000000),
                primaryContainer = Color(0xFF27272A),
                onPrimaryContainer = Color(0xFFFAFAFA),
                secondary = Color(0xFFA1A1AA),
                onSecondary = Color(0xFF09090B),
                background = Color(0xFF000000),
                onBackground = Color(0xFFFFFFFF),
                surface = Color(0xFF09090B),
                onSurface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFF18181B),
                onSurfaceVariant = Color(0xFFA1A1AA),
                cardBackground = Color(0xFF111114),
                cardBorderColor = Color(0xFF3F3F46),
                cardBorderWidth = 1.dp,
                cardShape = RoundedCornerShape(10.dp),
                cardElevation = 2.dp,
                backgroundBrush = Brush.verticalGradient(
                    listOf(Color(0xFF000000), Color(0xFF18181B))
                ),
                accentPillColor = Color(0xFFFFFFFF),
                successColor = Color(0xFFE4E4E7),
                warningColor = Color(0xFFD4D4D8),
                errorColor = Color(0xFFEF4444),
                infoColor = Color(0xFFA1A1AA)
            )

            AppThemeKey.REFINED_EDITORIAL -> JaynoCustomTheme(
                key = key,
                isDark = false,
                primary = Color(0xFF881337), // royal burgundy
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFFFE4E6),
                onPrimaryContainer = Color(0xFF4C0519),
                secondary = Color(0xFF9F1239),
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFFFAF7F2), // parchment
                onBackground = Color(0xFF2E1017),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF2E1017),
                surfaceVariant = Color(0xFFF3ECE6),
                onSurfaceVariant = Color(0xFF4C1D24),
                cardBackground = Color(0xFFFFFFFF),
                cardBorderColor = Color(0xFFE2D6CB),
                cardBorderWidth = 1.dp,
                cardShape = RoundedCornerShape(8.dp),
                cardElevation = 2.dp,
                backgroundBrush = Brush.verticalGradient(
                    listOf(Color(0xFFFAF7F2), Color(0xFFF5EFE6))
                ),
                accentPillColor = Color(0xFF881337),
                successColor = Color(0xFF15803D),
                warningColor = Color(0xFFB45309),
                errorColor = Color(0xFFBE123C),
                infoColor = Color(0xFF1D4ED8)
            )

            AppThemeKey.INDUSTRIAL_GEOMETRIC -> JaynoCustomTheme(
                key = key,
                isDark = true,
                primary = Color(0xFFFACC15), // safety yellow
                onPrimary = Color(0xFF18181B),
                primaryContainer = Color(0xFF713F12),
                onPrimaryContainer = Color(0xFFFEF08A),
                secondary = Color(0xFFEAB308),
                onSecondary = Color(0xFF18181B),
                background = Color(0xFF121214), // steel charcoal
                onBackground = Color(0xFFF4F4F5),
                surface = Color(0xFF1C1C21),
                onSurface = Color(0xFFFAFAFA),
                surfaceVariant = Color(0xFF27272E),
                onSurfaceVariant = Color(0xFFA1A1AA),
                cardBackground = Color(0xFF18181D),
                cardBorderColor = Color(0x66FACC15),
                cardBorderWidth = 1.5.dp,
                cardShape = RoundedCornerShape(6.dp), // geometric sharp
                cardElevation = 3.dp,
                backgroundBrush = Brush.verticalGradient(
                    listOf(Color(0xFF121214), Color(0xFF1E1E24))
                ),
                accentPillColor = Color(0xFFFACC15),
                successColor = Color(0xFF22C55E),
                warningColor = Color(0xFFF59E0B),
                errorColor = Color(0xFFEF4444),
                infoColor = Color(0xFF38BDF8)
            )

            AppThemeKey.HIGH_CONTRAST -> JaynoCustomTheme(
                key = key,
                isDark = true,
                primary = Color(0xFFFFFF00), // Pure luminous yellow
                onPrimary = Color(0xFF000000),
                primaryContainer = Color(0xFF333300),
                onPrimaryContainer = Color(0xFFFFFF00),
                secondary = Color(0xFFFFFFFF),
                onSecondary = Color(0xFF000000),
                background = Color(0xFF000000),
                onBackground = Color(0xFFFFFFFF),
                surface = Color(0xFF000000),
                onSurface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFF1A1A1A),
                onSurfaceVariant = Color(0xFFFFFFFF),
                cardBackground = Color(0xFF000000),
                cardBorderColor = Color(0xFFFFFFFF),
                cardBorderWidth = 2.dp,
                cardShape = RoundedCornerShape(4.dp),
                cardElevation = 0.dp,
                backgroundBrush = Brush.verticalGradient(
                    listOf(Color(0xFF000000), Color(0xFF000000))
                ),
                accentPillColor = Color(0xFFFFFF00),
                successColor = Color(0xFF00FF00),
                warningColor = Color(0xFFFFFF00),
                errorColor = Color(0xFFFF3333),
                infoColor = Color(0xFF00FFFF)
            )
        }
    }
}
