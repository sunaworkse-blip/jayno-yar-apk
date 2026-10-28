package com.example.jaynoyar.ui.dashboard

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.jaynoyar.calculator.AttendanceCalculator
import com.example.jaynoyar.calendar.DateUtils
import com.example.jaynoyar.calendar.JalaliDate
import com.example.jaynoyar.data.SeedData
import com.example.jaynoyar.data.WorkshopRepository
import com.example.jaynoyar.data.model.AttendanceEntity
import com.example.jaynoyar.data.model.EmployeeEntity
import com.example.jaynoyar.excel.XlsxEngine
import com.example.jaynoyar.ui.theme.LocalJaynoTheme
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun DashboardScreen(
    repository: WorkshopRepository,
    onNavigateToAttendance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val theme = LocalJaynoTheme.current

    val allEmployees by repository.activeEmployees.collectAsState(initial = emptyList())
    val allLeaves by repository.allLeaves.collectAsState(initial = emptyList())
    val recentAudits by repository.recentAuditLogs.collectAsState(initial = emptyList())

    val today = remember { LocalDate.now() }
    val todayAttendances by repository.getAttendancesForDateFlow(today.toString()).collectAsState(initial = emptyList())

    var periodFilter by remember { mutableStateOf("TODAY") } // TODAY, WEEK, MONTH
    var schedule by remember { mutableStateOf(SeedData.DEFAULT_SCHEDULE) }

    LaunchedEffect(Unit) {
        schedule = repository.getSchedule()
    }

    // Today calculations
    val todayBreakdowns = remember(allEmployees, todayAttendances, schedule) {
        val map = todayAttendances.associateBy { it.employeeId }
        allEmployees.map { emp ->
            val att = map[emp.id] ?: AttendanceEntity(
                employeeId = emp.id,
                workDate = today.toString(),
                checkInTime = null,
                checkOutTime = null,
                breakMinutes = schedule.lunchBreakMinutes
            )
            emp to AttendanceCalculator.computeBreakdown(att, schedule)
        }
    }

    val presentCount = todayBreakdowns.count { it.second.checkInTime != null }
    val absentCount = todayBreakdowns.count { it.second.isAbsent }
    val incompleteCount = todayBreakdowns.count { it.second.isIncomplete }
    val totalNetMinutes = todayBreakdowns.sumOf { it.second.netWorkingMinutes }
    val totalOvertimeMinutes = todayBreakdowns.sumOf { it.second.overtimeMinutes }
    val totalUndertimeMinutes = todayBreakdowns.sumOf { it.second.undertimeMinutes }
    val pendingLeavesCount = allLeaves.count { it.status == "PENDING" }

    // Department breakdown
    val deptData = remember(allEmployees) {
        allEmployees.groupBy { it.department }
            .map { (dept, list) -> dept to list.size }
            .sortedByDescending { it.second }
    }

    // Weekly Mock Trend Data
    val weeklyTrends = remember {
        listOf(
            DayTrend("شنبه", 120, 20),
            DayTrend("یکشنبه", 180, 0),
            DayTrend("دوشنبه", 90, 45),
            DayTrend("سه‌شنبه", 150, 10),
            DayTrend("چهارشنبه", 210, 0),
            DayTrend("پنج‌شنبه", 60, 30),
            DayTrend("جمعه", 0, 0)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundBrush)
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Title Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "داشبورد و گزارشات تحلیلی",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = theme.onBackground
                )
                Text(
                    text = "کارگاه مبلمان جاینو • ${JalaliDate.now().formatWithMonthName(true)}",
                    fontSize = 11.sp,
                    color = theme.primary
                )
            }

            // Export Monthly Workbook Button
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val file = XlsxEngine.exportAttendanceReportXlsx(context, todayBreakdowns, "گزارش_جامع_کارگاه_جاینو")
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "خروجی جامع اکسل کارگاه جاینو"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "خطا در خروجی اکسل: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("export_full_report_btn")
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("گزارش ماهانه اکسل", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 8 Key Metric KPI Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KpiCard(
                title = "پرسنل حاضر",
                value = "${DateUtils.toPersianDigits(presentCount.toString())} نفر",
                subtitle = "از ${allEmployees.size} نفر پرسنل",
                icon = Icons.Default.CheckCircle,
                iconColor = theme.successColor,
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                title = "غایبین امروز",
                value = "${DateUtils.toPersianDigits(absentCount.toString())} نفر",
                subtitle = "ثبت نشده در شیفت",
                icon = Icons.Default.Warning,
                iconColor = if (absentCount > 0) theme.errorColor else theme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KpiCard(
                title = "اضافه‌کار امروز",
                value = DateUtils.formatDurationMinutes(totalOvertimeMinutes),
                subtitle = "ساعات تاییدشده",
                icon = Icons.Default.MoreTime,
                iconColor = theme.primary,
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                title = "کسرکار امروز",
                value = DateUtils.formatDurationMinutes(totalUndertimeMinutes),
                subtitle = "شامل تاخیر و تعجیل",
                icon = Icons.Default.Schedule,
                iconColor = if (totalUndertimeMinutes > 0) theme.errorColor else theme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KpiCard(
                title = "کارکرد خالص",
                value = DateUtils.formatDurationMinutes(totalNetMinutes),
                subtitle = "خالص منهای استراحت",
                icon = Icons.Default.AssignmentTurnedIn,
                iconColor = theme.infoColor,
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                title = "مرخصی در انتظار",
                value = "${DateUtils.toPersianDigits(pendingLeavesCount.toString())} درخواست",
                subtitle = "نیازمند بررسی مدیر",
                icon = Icons.Default.DateRange,
                iconColor = if (pendingLeavesCount > 0) theme.warningColor else theme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Weekly Trends Canvas Chart Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = theme.cardShape,
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                WeeklyTrendBarChart(trends = weeklyTrends)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Department Distribution Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = theme.cardShape,
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                DepartmentWorkloadChart(deptData = deptData)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Recent Audit Activity Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = theme.cardShape,
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("آخرین رخدادهای سامانه و ممیزی", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = theme.onBackground)
                    Icon(Icons.Default.History, contentDescription = null, tint = theme.primary, modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                recentAudits.take(4).forEach { audit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = theme.surfaceVariant,
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            Text(
                                text = audit.actionType,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.primary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = audit.description,
                            fontSize = 11.sp,
                            color = theme.onSurfaceVariant,
                            maxLines = 1,
                            modifier = Modifier.weight(1f).padding(start = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    val theme = LocalJaynoTheme.current

    Card(
        modifier = modifier,
        shape = theme.cardShape,
        colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
        border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = theme.cardElevation)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, color = theme.onSurfaceVariant)
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = theme.onBackground
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = theme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
