package com.example.jaynoyar.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jaynoyar.calendar.DateUtils
import com.example.jaynoyar.ui.theme.LocalJaynoTheme

data class DayTrend(
    val dayLabel: String,
    val overtimeMinutes: Int,
    val undertimeMinutes: Int
)

@Composable
fun WeeklyTrendBarChart(
    trends: List<DayTrend>,
    modifier: Modifier = Modifier
) {
    val theme = LocalJaynoTheme.current

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "روند مقایسه‌ای اضافه‌کار و کسرکار (هفتگی)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = theme.onBackground
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LegendIndicator("اضافه‌کار", theme.successColor)
                LegendIndicator("کسرکار", theme.errorColor)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val maxMinutes = trends.maxOfOrNull { maxOf(it.overtimeMinutes, it.undertimeMinutes) }?.coerceAtLeast(120) ?: 120
                val barWidth = 14.dp.toPx()
                val spacing = size.width / trends.size.coerceAtLeast(1)

                trends.forEachIndexed { i, t ->
                    val cx = i * spacing + spacing / 2f
                    val chartHeight = size.height - 24f

                    // Baseline
                    drawLine(
                        color = Color(0x3094A3B8),
                        start = Offset(0f, chartHeight),
                        end = Offset(size.width, chartHeight),
                        strokeWidth = 1f
                    )

                    // Overtime bar (green)
                    val otHeight = (t.overtimeMinutes / maxMinutes.toFloat()) * (chartHeight - 10f)
                    if (otHeight > 0) {
                        drawRoundRect(
                            color = theme.successColor,
                            topLeft = Offset(cx - barWidth - 2f, chartHeight - otHeight),
                            size = Size(barWidth, otHeight),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }

                    // Undertime bar (red)
                    val utHeight = (t.undertimeMinutes / maxMinutes.toFloat()) * (chartHeight - 10f)
                    if (utHeight > 0) {
                        drawRoundRect(
                            color = theme.errorColor,
                            topLeft = Offset(cx + 2f, chartHeight - utHeight),
                            size = Size(barWidth, utHeight),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }
                }
            }
        }

        // Labels under bars
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            trends.forEach { t ->
                Text(
                    text = t.dayLabel,
                    fontSize = 10.sp,
                    color = theme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DepartmentWorkloadChart(
    deptData: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    val theme = LocalJaynoTheme.current
    val total = deptData.sumOf { it.second }.coerceAtLeast(1)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "توزیع نیروی کار در سالن‌های کارگاه مبلمان",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = theme.onBackground,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        deptData.forEach { (dept, count) ->
            val fraction = count / total.toFloat()
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(dept, fontSize = 11.sp, color = theme.onBackground)
                    Text("${DateUtils.toPersianDigits(count.toString())} نفر (${(fraction * 100).toInt()}٪)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.primary)
                }

                Spacer(modifier = Modifier.height(3.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(theme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(theme.primary)
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendIndicator(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, color = Color.Gray)
    }
}
