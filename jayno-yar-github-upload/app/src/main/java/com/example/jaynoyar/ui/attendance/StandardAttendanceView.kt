package com.example.jaynoyar.ui.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.jaynoyar.calculator.AttendanceCalculationBreakdown
import com.example.jaynoyar.calendar.DateUtils
import com.example.jaynoyar.data.model.AttendanceEntity
import com.example.jaynoyar.data.model.EmployeeEntity
import com.example.jaynoyar.ui.theme.LocalJaynoTheme

data class EmployeeAttendanceRow(
    val employee: EmployeeEntity,
    val attendance: AttendanceEntity?,
    val breakdown: AttendanceCalculationBreakdown
)

@Composable
fun StandardAttendanceView(
    rows: List<EmployeeAttendanceRow>,
    onSaveAttendance: (AttendanceEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalJaynoTheme.current
    var selectedRowForAudit by remember { mutableStateOf<EmployeeAttendanceRow?>(null) }
    var editingRow by remember { mutableStateOf<EmployeeAttendanceRow?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(rows, key = { it.employee.id }) { row ->
            AttendanceCardItem(
                row = row,
                onInspect = { selectedRowForAudit = row },
                onEdit = { editingRow = row }
            )
        }
    }

    // Inspection Modal
    selectedRowForAudit?.let { row ->
        CalculationAuditDialog(
            employee = row.employee,
            breakdown = row.breakdown,
            onDismiss = { selectedRowForAudit = null }
        )
    }

    // Quick Edit Attendance Modal
    editingRow?.let { row ->
        EditAttendanceDialog(
            row = row,
            onDismiss = { editingRow = null },
            onSave = { updated ->
                onSaveAttendance(updated)
                editingRow = null
            }
        )
    }
}

@Composable
fun AttendanceCardItem(
    row: EmployeeAttendanceRow,
    onInspect: () -> Unit,
    onEdit: () -> Unit
) {
    val theme = LocalJaynoTheme.current
    val b = row.breakdown
    val att = row.attendance

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("attendance_card_${row.employee.id}"),
        shape = theme.cardShape,
        colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
        border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = theme.cardElevation)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Avatar + Name + Department + Edit Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(theme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = row.employee.firstName.take(1),
                            color = theme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = row.employee.fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = theme.onBackground
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(${row.employee.id})",
                                fontSize = 11.sp,
                                color = theme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${row.employee.department} • ${row.employee.role}",
                            fontSize = 11.sp,
                            color = theme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.testTag("edit_attendance_btn_${row.employee.id}")
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "ویرایش تردد",
                        tint = theme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Timestamps row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("ورود:", fontSize = 10.sp, color = theme.onSurfaceVariant)
                    Text(
                        text = b.checkInTime ?: "ثبت نشده",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (b.checkInTime != null) theme.onBackground else theme.errorColor
                    )
                }

                Column {
                    Text("خروج:", fontSize = 10.sp, color = theme.onSurfaceVariant)
                    Text(
                        text = b.checkOutTime ?: "ثبت نشده",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (b.checkOutTime != null) theme.onBackground else theme.warningColor
                    )
                }

                Column {
                    Text("استراحت:", fontSize = 10.sp, color = theme.onSurfaceVariant)
                    Text(
                        text = "${DateUtils.toPersianDigits(b.unpaidBreakMinutes.toString())} دقیقه",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = theme.onBackground
                    )
                }

                Column {
                    Text("کارکرد خالص:", fontSize = 10.sp, color = theme.onSurfaceVariant)
                    Text(
                        text = DateUtils.formatDurationMinutes(b.netWorkingMinutes),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = theme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badges row: Overtime, Undertime, Lateness, Incomplete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (b.overtimeMinutes > 0) {
                        BadgePill(
                            text = "+${DateUtils.formatDurationMinutes(b.overtimeMinutes)} اضافه‌کار",
                            bgColor = theme.successColor.copy(alpha = 0.15f),
                            textColor = theme.successColor
                        )
                    }

                    if (b.undertimeMinutes > 0 && !b.isIncomplete) {
                        BadgePill(
                            text = "-${DateUtils.formatDurationMinutes(b.undertimeMinutes)} کسرکار",
                            bgColor = theme.errorColor.copy(alpha = 0.15f),
                            textColor = theme.errorColor
                        )
                    }

                    if (b.latenessMinutes > 0) {
                        BadgePill(
                            text = "${DateUtils.toPersianDigits(b.latenessMinutes.toString())} د تاخیر",
                            bgColor = theme.warningColor.copy(alpha = 0.15f),
                            textColor = theme.warningColor
                        )
                    }

                    if (b.isIncomplete) {
                        BadgePill(
                            text = "تردد ناقص",
                            bgColor = theme.errorColor.copy(alpha = 0.2f),
                            textColor = theme.errorColor
                        )
                    }
                }

                // Inspect calculation button
                OutlinedButton(
                    onClick = onInspect,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.primary),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("inspect_calc_btn_${row.employee.id}")
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(14.dp), tint = theme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ریز محاسبات", fontSize = 11.sp, color = theme.primary)
                }
            }
        }
    }
}

@Composable
fun BadgePill(text: String, bgColor: Color, textColor: Color) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun EditAttendanceDialog(
    row: EmployeeAttendanceRow,
    onDismiss: () -> Unit,
    onSave: (AttendanceEntity) -> Unit
) {
    val theme = LocalJaynoTheme.current
    val currentAtt = row.attendance

    var checkIn by remember { mutableStateOf(currentAtt?.checkInTime ?: "08:00") }
    var checkOut by remember { mutableStateOf(currentAtt?.checkOutTime ?: "17:00") }
    var breakMinutes by remember { mutableStateOf((currentAtt?.breakMinutes ?: 45).toString()) }
    var isBreakPaid by remember { mutableStateOf(currentAtt?.isBreakPaid ?: false) }
    var isOvernight by remember { mutableStateOf(currentAtt?.isOvernight ?: false) }
    var overtimeApproved by remember { mutableStateOf(currentAtt?.overtimeApproved ?: true) }
    var note by remember { mutableStateOf(currentAtt?.note ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = theme.cardBackground,
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("edit_attendance_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "ثبت / ویرایش تردد پرسنل",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = theme.onBackground
                )
                Text(
                    text = "${row.employee.fullName} • تاریخ: ${row.breakdown.dateString}",
                    fontSize = 12.sp,
                    color = theme.primary,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = checkIn,
                        onValueChange = { checkIn = it },
                        label = { Text("ساعت ورود (HH:mm)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("check_in_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = checkOut,
                        onValueChange = { checkOut = it },
                        label = { Text("ساعت خروج (HH:mm)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("check_out_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = breakMinutes,
                    onValueChange = { breakMinutes = it },
                    label = { Text("مدت استراحت ناهار (دقیقه)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("break_minutes_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isOvernight, onCheckedChange = { isOvernight = it })
                    Text("شیفت شبانه (گذر از نیمه‌شب)", fontSize = 12.sp, color = theme.onBackground)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = overtimeApproved, onCheckedChange = { overtimeApproved = it })
                    Text("اضافه‌کار رسمی و تایید شده توسط کارگاه", fontSize = 12.sp, color = theme.onBackground)
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("توضیحات و علت تغییر") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("انصراف")
                    }

                    Button(
                        onClick = {
                            val updated = AttendanceEntity(
                                id = currentAtt?.id ?: 0,
                                employeeId = row.employee.id,
                                workDate = row.breakdown.dateString,
                                checkInTime = if (checkIn.isBlank() || checkIn == "-") null else checkIn.trim(),
                                checkOutTime = if (checkOut.isBlank() || checkOut == "-") null else checkOut.trim(),
                                breakMinutes = breakMinutes.toIntOrNull() ?: 45,
                                isBreakPaid = isBreakPaid,
                                isOvernight = isOvernight,
                                overtimeApproved = overtimeApproved,
                                note = note.ifBlank { null }
                            )
                            onSave(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_attendance_btn")
                    ) {
                        Text("ذخیره تردد")
                    }
                }
            }
        }
    }
}
