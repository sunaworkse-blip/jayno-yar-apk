package com.example.jaynoyar.ui.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jaynoyar.calculator.AttendanceCalculator
import com.example.jaynoyar.calendar.DateUtils
import com.example.jaynoyar.data.model.AttendanceEntity
import com.example.jaynoyar.data.model.WorkScheduleEntity
import com.example.jaynoyar.ui.theme.LocalJaynoTheme

data class SpreadsheetDraftRow(
    val employeeId: String,
    val employeeName: String,
    val department: String,
    var checkIn: String,
    var checkOut: String,
    var breakMin: String,
    var isDirty: Boolean = false
)

@Composable
fun SpreadsheetView(
    rows: List<EmployeeAttendanceRow>,
    schedule: WorkScheduleEntity,
    selectedDate: String,
    onSaveBatch: (List<AttendanceEntity>) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalJaynoTheme.current
    val hScrollState = rememberScrollState()

    // Working in-memory editable spreadsheet state
    val draftRows = remember(rows) {
        rows.map { r ->
            SpreadsheetDraftRow(
                employeeId = r.employee.id,
                employeeName = r.employee.fullName,
                department = r.employee.department,
                checkIn = r.attendance?.checkInTime ?: "",
                checkOut = r.attendance?.checkOutTime ?: "",
                breakMin = (r.attendance?.breakMinutes ?: 45).toString(),
                isDirty = false
            )
        }.toMutableList()
    }

    var localDrafts by remember { mutableStateOf(draftRows) }
    var hasDirtyChanges by remember { mutableStateOf(false) }
    var showBatchConfirmDialog by remember { mutableStateOf(false) }
    var selectedRowForAudit by remember { mutableStateOf<EmployeeAttendanceRow?>(null) }

    // Column widths
    val colCode = 80.dp
    val colName = 130.dp
    val colDept = 100.dp
    val colCheckIn = 85.dp
    val colCheckOut = 85.dp
    val colBreak = 75.dp
    val colNet = 95.dp
    val colOvertime = 95.dp
    val colStatus = 100.dp

    Column(modifier = modifier.fillMaxWidth()) {
        // Toolbar with Bulk Operations, Save, Reset
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = theme.cardShape,
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { showBatchConfirmDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("batch_fill_btn")
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp), tint = theme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تکمیل شیفت استاندارد همه", fontSize = 11.sp, color = theme.primary)
                    }

                    if (hasDirtyChanges) {
                        Surface(
                            color = theme.warningColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "تغییرات ذخیره‌نشده",
                                color = theme.warningColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Reset / Undo
                    OutlinedButton(
                        onClick = {
                            localDrafts = rows.map { r ->
                                SpreadsheetDraftRow(
                                    employeeId = r.employee.id,
                                    employeeName = r.employee.fullName,
                                    department = r.employee.department,
                                    checkIn = r.attendance?.checkInTime ?: "",
                                    checkOut = r.attendance?.checkOutTime ?: "",
                                    breakMin = (r.attendance?.breakMinutes ?: 45).toString(),
                                    isDirty = false
                                )
                            }.toMutableList()
                            hasDirtyChanges = false
                        },
                        enabled = hasDirtyChanges,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("reset_spreadsheet_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("بازنشانی", fontSize = 11.sp)
                    }

                    // Save Batch Button
                    Button(
                        onClick = {
                            val attendancesToSave = localDrafts.map { d ->
                                val orig = rows.find { it.employee.id == d.employeeId }?.attendance
                                AttendanceEntity(
                                    id = orig?.id ?: 0,
                                    employeeId = d.employeeId,
                                    workDate = selectedDate,
                                    checkInTime = if (d.checkIn.isBlank() || d.checkIn == "-") null else d.checkIn.trim(),
                                    checkOutTime = if (d.checkOut.isBlank() || d.checkOut == "-") null else d.checkOut.trim(),
                                    breakMinutes = d.breakMin.toIntOrNull() ?: 45,
                                    isBreakPaid = orig?.isBreakPaid ?: false,
                                    isOvernight = orig?.isOvernight ?: false,
                                    overtimeApproved = orig?.overtimeApproved ?: true,
                                    note = orig?.note
                                )
                            }
                            onSaveBatch(attendancesToSave)
                            hasDirtyChanges = false
                            localDrafts = localDrafts.map { it.copy(isDirty = false) }.toMutableList()
                        },
                        enabled = hasDirtyChanges,
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("save_spreadsheet_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ذخیره تغییرات", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2D Scrollable Table Container
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = theme.cardShape,
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(hScrollState)
            ) {
                // Table Header
                Row(
                    modifier = Modifier
                        .background(theme.primaryContainer)
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCell(text = "کد", width = colCode, isHeader = true, color = theme.onPrimaryContainer)
                    TableCell(text = "نام پرسنل", width = colName, isHeader = true, color = theme.onPrimaryContainer)
                    TableCell(text = "واحد کارگاه", width = colDept, isHeader = true, color = theme.onPrimaryContainer)
                    TableCell(text = "ساعت ورود", width = colCheckIn, isHeader = true, color = theme.onPrimaryContainer)
                    TableCell(text = "ساعت خروج", width = colCheckOut, isHeader = true, color = theme.onPrimaryContainer)
                    TableCell(text = "استراحت (د)", width = colBreak, isHeader = true, color = theme.onPrimaryContainer)
                    TableCell(text = "کارکرد خالص", width = colNet, isHeader = true, color = theme.onPrimaryContainer)
                    TableCell(text = "اضافه‌کار", width = colOvertime, isHeader = true, color = theme.onPrimaryContainer)
                    TableCell(text = "وضعیت", width = colStatus, isHeader = true, color = theme.onPrimaryContainer)
                }

                // Table Rows
                LazyColumn(
                    modifier = Modifier.height(420.dp)
                ) {
                    itemsIndexed(localDrafts) { index, draft ->
                        val origRow = rows.find { it.employee.id == draft.employeeId }
                        val isEven = index % 2 == 0

                        // Live calculations from draft
                        val cinMin = AttendanceCalculator.parseTimeToMinutes(draft.checkIn)
                        val coutMin = AttendanceCalculator.parseTimeToMinutes(draft.checkOut)
                        val bMin = draft.breakMin.toIntOrNull() ?: 45
                        val gross = AttendanceCalculator.calculateGrossDurationMinutes(cinMin, coutMin)
                        val net = AttendanceCalculator.calculateNetDurationMinutes(gross, bMin)
                        val expected = schedule.dailyExpectedMinutes
                        val ot = AttendanceCalculator.calculateDailyOvertimeMinutes(net, expected)
                        val ut = AttendanceCalculator.calculateDailyUndertimeMinutes(net, expected)
                        val isIncomplete = (cinMin != null && coutMin == null) || (cinMin == null && coutMin != null)

                        val rowBg = when {
                            draft.isDirty -> theme.warningColor.copy(alpha = 0.08f)
                            isIncomplete -> theme.errorColor.copy(alpha = 0.06f)
                            !isEven -> theme.surfaceVariant.copy(alpha = 0.35f)
                            else -> Color.Transparent
                        }

                        Row(
                            modifier = Modifier
                                .background(rowBg)
                                .border(
                                    width = 0.5.dp,
                                    color = if (draft.isDirty) theme.warningColor.copy(alpha = 0.4f) else theme.cardBorderColor.copy(alpha = 0.3f)
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableCell(text = draft.employeeId, width = colCode, color = theme.onSurfaceVariant)
                            TableCell(
                                text = draft.employeeName,
                                width = colName,
                                fontWeight = FontWeight.Bold,
                                color = theme.onBackground,
                                onClick = { origRow?.let { selectedRowForAudit = it } }
                            )
                            TableCell(text = draft.department, width = colDept, color = theme.onSurfaceVariant, fontSize = 10.sp)

                            // Editable Check-In
                            EditableTableCell(
                                value = draft.checkIn,
                                placeholder = "--:--",
                                width = colCheckIn,
                                isError = draft.checkIn.isNotBlank() && cinMin == null,
                                onValueChange = { newVal ->
                                    draft.checkIn = newVal
                                    draft.isDirty = true
                                    hasDirtyChanges = true
                                    localDrafts = localDrafts.toMutableList()
                                }
                            )

                            // Editable Check-Out
                            EditableTableCell(
                                value = draft.checkOut,
                                placeholder = "--:--",
                                width = colCheckOut,
                                isError = isIncomplete || (draft.checkOut.isNotBlank() && coutMin == null),
                                onValueChange = { newVal ->
                                    draft.checkOut = newVal
                                    draft.isDirty = true
                                    hasDirtyChanges = true
                                    localDrafts = localDrafts.toMutableList()
                                }
                            )

                            // Editable Break Minutes
                            EditableTableCell(
                                value = draft.breakMin,
                                placeholder = "۴۵",
                                width = colBreak,
                                onValueChange = { newVal ->
                                    draft.breakMin = newVal
                                    draft.isDirty = true
                                    hasDirtyChanges = true
                                    localDrafts = localDrafts.toMutableList()
                                }
                            )

                            // Live Net
                            TableCell(
                                text = if (net > 0) DateUtils.formatDurationMinutes(net) else "-",
                                width = colNet,
                                fontWeight = FontWeight.Bold,
                                color = if (net >= expected) theme.primary else theme.onBackground
                            )

                            // Live Overtime
                            TableCell(
                                text = if (ot > 0) "+${DateUtils.formatDurationMinutes(ot)}" else if (ut > 0 && !isIncomplete) "-${DateUtils.formatDurationMinutes(ut)}" else "-",
                                width = colOvertime,
                                fontWeight = FontWeight.Bold,
                                color = if (ot > 0) theme.successColor else if (ut > 0) theme.errorColor else theme.onSurfaceVariant
                            )

                            // Status Tag
                            Box(
                                modifier = Modifier
                                    .width(colStatus)
                                    .padding(horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    isIncomplete -> BadgePill("تردد ناقص", theme.errorColor.copy(alpha = 0.2f), theme.errorColor)
                                    ot > 0 -> BadgePill("اضافه‌کار", theme.successColor.copy(alpha = 0.15f), theme.successColor)
                                    cinMin != null && coutMin != null -> BadgePill("کامل", theme.primary.copy(alpha = 0.15f), theme.primary)
                                    else -> BadgePill("ثبت نشده", theme.surfaceVariant, theme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Batch Auto-fill Confirmation Dialog
    if (showBatchConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showBatchConfirmDialog = false },
            title = { Text("تکمیل خودکار شیفت استاندارد") },
            text = {
                Text(
                    "آیا مایلید برای تمامی پرسنل، ساعت ورود ۰۸:۰۰ و ساعت خروج ۱۷:۰۰ با ۴۵ دقیقه استراحت ناهار تنظیم شود؟ " +
                            "تغییرات پس از بازبینی و زدن دکمه «ذخیره تغییرات» نهایی خواهند شد."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        localDrafts = localDrafts.map { d ->
                            d.copy(
                                checkIn = "08:00",
                                checkOut = "17:00",
                                breakMin = "45",
                                isDirty = true
                            )
                        }.toMutableList()
                        hasDirtyChanges = true
                        showBatchConfirmDialog = false
                    }
                ) {
                    Text("بله، اعمال برای همه")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBatchConfirmDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Inspect Modal
    selectedRowForAudit?.let { row ->
        CalculationAuditDialog(
            employee = row.employee,
            breakdown = row.breakdown,
            onDismiss = { selectedRowForAudit = null }
        )
    }
}

@Composable
private fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isHeader: Boolean = false,
    fontWeight: FontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
    fontSize: androidx.compose.ui.unit.TextUnit = if (isHeader) 11.sp else 11.sp,
    color: Color = Color.Unspecified,
    onClick: (() -> Unit)? = null
) {
    Text(
        text = text,
        modifier = Modifier
            .width(width)
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color,
        textAlign = TextAlign.Center,
        maxLines = 1
    )
}

@Composable
private fun EditableTableCell(
    value: String,
    placeholder: String,
    width: androidx.compose.ui.unit.Dp,
    isError: Boolean = false,
    onValueChange: (String) -> Unit
) {
    val theme = LocalJaynoTheme.current
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .border(
                width = 1.dp,
                color = if (isError) theme.errorColor else theme.cardBorderColor.copy(alpha = 0.6f),
                shape = RoundedCornerShape(4.dp)
            )
            .background(
                if (isError) theme.errorColor.copy(alpha = 0.08f) else theme.surfaceVariant.copy(alpha = 0.25f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (value.isBlank()) {
            Text(text = placeholder, color = Color.Gray, fontSize = 11.sp, textAlign = TextAlign.Center)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = if (isError) theme.errorColor else theme.onBackground,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            ),
            cursorBrush = SolidColor(theme.primary),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
