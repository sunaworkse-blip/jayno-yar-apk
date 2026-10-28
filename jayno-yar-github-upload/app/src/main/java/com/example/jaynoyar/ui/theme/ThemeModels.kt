package com.example.jaynoyar.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AppThemeKey(
    val titleFa: String,
    val subtitleFa: String,
    val isDark: Boolean
) {
    MINIMAL_LIGHT("روشن مینیمال", "سفید و خاکستری مدرن با وضوح بالا", false),
    PREMIUM_DARK("تاریک لوکس", "مشکی آبنوسی با خطوط طلایی و کهربایی", true),
    WORKSHOP_WOOD("چوب و هنر کارگاه", "گردویی، بلوطی و چرم طبیعی مبلمان", true),
    GLASSMORPHISM("شیشه‌ای مدرن", "پس‌زمینه بلوری با هاله نیلی و ارغوانی", true),
    ENTERPRISE_NAVY("سازمانی اداری", "سرمه‌ای رسمی با ساختار مهندسی دقیق", true),
    SOFT_NEUTRAL("شن و کتان خنثی", "کاغذی کرم و خاکی با جزئیات سفالین", false),
    DEEP_SAPPHIRE("لاجوردی عمیق", "آبی درباری ایرانی و درخشش فیروزه‌ای", true),
    FIROUZEH_EMERALD("فیروزه و زمرد", "ترکیب اصیل فیروزه نیشابور و سبز یشمی", false),
    ELEGANTE_MONOCHROME("تک‌رنگ فاخر", "کنتراست خالص سفید، تیتانیوم و مشکی", true),
    REFINED_EDITORIAL("عنابی و کاغذی", "قرمز شرابی و بافت پرینت تحلیلی", false),
    INDUSTRIAL_GEOMETRIC("صنعتی و خط‌کشی", "زغال‌سنگی با رگه‌های زرد هشدار ایمنی", true),
    HIGH_CONTRAST("کنتراست بالا دسترس‌پذیر", "سیاه و زرد مطلق برای حداکثر خوانایی", true)
}

data class JaynoCustomTheme(
    val key: AppThemeKey,
    val isDark: Boolean,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val cardBackground: Color,
    val cardBorderColor: Color,
    val cardBorderWidth: Dp,
    val cardShape: Shape,
    val cardElevation: Dp,
    val backgroundBrush: Brush,
    val accentPillColor: Color,
    val successColor: Color,
    val warningColor: Color,
    val errorColor: Color,
    val infoColor: Color
)
