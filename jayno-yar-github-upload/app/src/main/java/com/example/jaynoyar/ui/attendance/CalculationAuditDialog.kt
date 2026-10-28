package com.example.jaynoyar.ui.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.jaynoyar.calculator.AttendanceCalculationBreakdown
import com.example.jaynoyar.calendar.DateUtils
import com.example.jaynoyar.data.model.EmployeeEntity
import com.example.jaynoyar.ui.theme.LocalJaynoTheme

@Composable
fun CalculationAuditDialog(
    employee: EmployeeEntity,
    breakdown: AttendanceCalculationBreakdown,
    onDismiss: () -> Unit
) {
    val theme = LocalJaynoTheme.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = theme.cardBackground,
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .testTag("calculation_audit_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "شفافیت و فرمول محاسبه کارکرد",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = theme.onBackground
                        )
                        Text(
                            text = "${employee.fullName} (${employee.id}) • ${breakdown.dateString}",
                            fontSize = 12.sp,
                            color = theme.primary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.onSurfaceVariant)
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = theme.cardBorderColor
                )

                // Warning if incomplete
                if (breakdown.isIncomplete) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = theme.errorColor.copy(alpha = 0.12f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = theme.errorColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "هشدار: تردد ناقص است (خروج ثبت نشده). به صورت خودکار تمام شیفت کسرکار در نظر گرفته شده تا زمان تکمیل.",
                                fontSize = 11.sp,
                                color = theme.errorColor,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Step 1: Raw Timestamps
                SectionHeader(title = "۱. زمان‌های ثبت‌شده ورود و خروج")
                AuditRow("ساعت ورود ثبت‌شده:", breakdown.checkInTime ?: "ثبت نشده")
                AuditRow("ساعت خروج ثبت‌شده:", breakdown.checkOutTime ?: "ثبت نشده")
                AuditRow("مدت کل حضور ناخالص:", DateUtils.formatDurationMinutes(breakdown.grossMinutes))

                Spacer(modifier = Modifier.height(10.dp))

                // Step 2: Break Policy
                SectionHeader(title = "۲. محاسبات وقت استراحت و ناهار")
                AuditRow("استراحت ناهار (کسر شده از کارکرد):", "${DateUtils.toPersianDigits(breakdown.unpaidBreakMinutes.toString())} دقیقه")
                AuditRow("استراحت چای (محاسبه به عنوان کارکرد موظف):", "${DateUtils.toPersianDigits(breakdown.paidBreakMinutes.toString())} دقیقه")
                AuditRow("کارکرد خالص نهایی:", DateUtils.formatDurationMinutes(breakdown.netWorkingMinutes))

                Spacer(modifier = Modifier.height(10.dp))

                // Step 3: Expected Schedule & Leaves
                SectionHeader(title = "۳. شیفت موظفی و مرخصی‌ها")
                AuditRow("ساعت موظفی طبق برنامه کاری:", DateUtils.formatDurationMinutes(breakdown.expectedMinutes))
                if (breakdown.leaveAdjustmentMinutes > 0) {
                    AuditRow("تعدیل مرخصی تایید شده:", "${DateUtils.formatDurationMinutes(breakdown.leaveAdjustmentMinutes)} (${breakdown.leaveTypeApplied ?: ""})")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Step 4: Final Overtime / Undertime Outcome
                SectionHeader(title = "۴. نتیجه مقایسه نهایی و اضافه‌کار/کسرکار")
                AuditRow(
                    label = "اضافه‌کار محاسبه‌شده:",
                    value = DateUtils.formatDurationMinutes(breakdown.overtimeMinutes),
                    valueColor = if (breakdown.overtimeMinutes > 0) theme.successColor else theme.onBackground
                )
                AuditRow(
                    label = "کسرکار محاسبه‌شده:",
                    value = DateUtils.formatDurationMinutes(breakdown.undertimeMinutes),
                    valueColor = if (breakdown.undertimeMinutes > 0) theme.errorColor else theme.onBackground
                )
                AuditRow(
                    label = "وضعیت تایید اضافه‌کار:",
                    value = if (breakdown.isOvertimeApproved) "تایید رسمی مدیریت" else "تایید نشده",
                    valueColor = if (breakdown.isOvertimeApproved) theme.successColor else theme.warningColor
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Step 5: Punctuality Deviations (independent of overtime)
                SectionHeader(title = "۵. انحرافات انضباطی و نظم تردد (مستقل از اضافه‌کار)")
                AuditRow(
                    label = "تاخیر در ورود:",
                    value = if (breakdown.latenessMinutes > 0) "${DateUtils.toPersianDigits(breakdown.latenessMinutes.toString())} دقیقه تاخیر" else "به‌موقع (بدون تاخیر)",
                    valueColor = if (breakdown.latenessMinutes > 0) theme.warningColor else theme.successColor
                )
                AuditRow(
                    label = "تعجیل در خروج:",
                    value = if (breakdown.earlyDepartureMinutes > 0) "${DateUtils.toPersianDigits(breakdown.earlyDepartureMinutes.toString())} دقیقه خروج زودهنگام" else "خروج به‌موقع",
                    valueColor = if (breakdown.earlyDepartureMinutes > 0) theme.warningColor else theme.successColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Rule explanation box
                Card(
                    colors = CardDefaults.cardColors(containerColor = theme.surfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = theme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("قانون محاسباتی اعمال‌شده:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = theme.primary)
                        }
                        Text(
                            text = breakdown.appliedRuleDescription,
                            fontSize = 11.sp,
                            color = theme.onSurfaceVariant,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("close_audit_dialog_btn")
                ) {
                    Text("بستن و تایید بررسی")
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = LocalJaynoTheme.current.primary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun AuditRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    val theme = LocalJaynoTheme.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = theme.onSurfaceVariant)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (valueColor != Color.Unspecified) valueColor else theme.onBackground
        )
    }
}
