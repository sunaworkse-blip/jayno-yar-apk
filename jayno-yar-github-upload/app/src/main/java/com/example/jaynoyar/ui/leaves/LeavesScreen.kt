package com.example.jaynoyar.ui.leaves

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.jaynoyar.calendar.DateUtils
import com.example.jaynoyar.calendar.JalaliDate
import com.example.jaynoyar.data.WorkshopRepository
import com.example.jaynoyar.data.model.EmployeeEntity
import com.example.jaynoyar.data.model.LeaveRecordEntity
import com.example.jaynoyar.ui.attendance.BadgePill
import com.example.jaynoyar.ui.theme.LocalJaynoTheme
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun LeavesScreen(
    repository: WorkshopRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val theme = LocalJaynoTheme.current

    val allLeaves by repository.allLeaves.collectAsState(initial = emptyList())
    val employees by repository.activeEmployees.collectAsState(initial = emptyList())
    val empMap = remember(employees) { employees.associateBy { it.id } }

    var statusFilter by remember { mutableStateOf("همه") }
    var showRequestDialog by remember { mutableStateOf(false) }

    val filteredLeaves = remember(allLeaves, statusFilter) {
        if (statusFilter == "همه") allLeaves else allLeaves.filter { it.status == statusFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundBrush)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // Header & Request Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "مدیریت مرخصی و غیبت‌ها",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = theme.onBackground
                )
                Text(
                    text = "${allLeaves.count { it.status == "PENDING" }} درخواست در انتظار بررسی",
                    fontSize = 11.sp,
                    color = theme.primary
                )
            }

            Button(
                onClick = { showRequestDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("request_leave_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("ثبت مرخصی", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Status Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val filters = listOf("همه", "APPROVED", "PENDING", "REJECTED")
            items(filters) { f ->
                val label = when (f) {
                    "APPROVED" -> "تایید شده"
                    "PENDING" -> "در انتظار"
                    "REJECTED" -> "رد شده"
                    else -> "همه"
                }
                val isSel = statusFilter == f
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSel) theme.primary else theme.surfaceVariant,
                    modifier = Modifier.clickable { statusFilter = f }
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) theme.onPrimary else theme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Leaves List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredLeaves, key = { it.id }) { leave ->
                val emp = empMap[leave.employeeId]
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
                            Column {
                                Text(
                                    text = emp?.fullName ?: leave.employeeId,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = theme.onBackground
                                )
                                Text(
                                    text = "${emp?.department ?: ""} • ${leave.employeeId}",
                                    fontSize = 11.sp,
                                    color = theme.onSurfaceVariant
                                )
                            }

                            when (leave.status) {
                                "APPROVED" -> BadgePill("تایید شده", theme.successColor.copy(alpha = 0.15f), theme.successColor)
                                "PENDING" -> BadgePill("در انتظار بررسی", theme.warningColor.copy(alpha = 0.2f), theme.warningColor)
                                "REJECTED" -> BadgePill("رد شده", theme.errorColor.copy(alpha = 0.15f), theme.errorColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val typeLabel = when (leave.leaveType) {
                            "DAILY" -> "مرخصی روزانه"
                            "HOURLY" -> "مرخصی ساعتی (${leave.startTime ?: ""} تا ${leave.endTime ?: ""})"
                            "SICK" -> "استعلاجی پزشکی"
                            "MISSION" -> "ماموریت کاری بیرون کارگاه"
                            "UNPAID" -> "مرخصی بدون حقوق"
                            else -> leave.leaveType
                        }

                        val jStart = try { JalaliDate.fromLocalDate(LocalDate.parse(leave.startDate)).formatFormatted(false) } catch (e: Exception) { leave.startDate }
                        val jEnd = try { JalaliDate.fromLocalDate(LocalDate.parse(leave.endDate)).formatFormatted(false) } catch (e: Exception) { leave.endDate }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(theme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(typeLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = theme.primary)
                            Text("از $jStart تا $jEnd", fontSize = 11.sp, color = theme.onSurfaceVariant)
                        }

                        if (leave.reason.isNotBlank()) {
                            Text(
                                text = "علت: ${leave.reason}",
                                fontSize = 11.sp,
                                color = theme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }

                        // Approval action buttons for pending leaves
                        if (leave.status == "PENDING") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            repository.updateLeaveStatus(leave.id, "REJECTED", leave.employeeId)
                                            Toast.makeText(context, "درخواست رد شد", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = theme.errorColor, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("رد درخواست", fontSize = 11.sp, color = theme.errorColor)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        scope.launch {
                                            repository.updateLeaveStatus(leave.id, "APPROVED", leave.employeeId)
                                            Toast.makeText(context, "مرخصی با موفقیت تایید شد", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = theme.successColor),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تایید مرخصی", fontSize = 11.sp, color = androidx.compose.ui.graphics.Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Request Leave Dialog
    if (showRequestDialog) {
        RequestLeaveDialog(
            employees = employees,
            onDismiss = { showRequestDialog = false },
            onSubmit = { newLeave ->
                scope.launch {
                    repository.submitLeave(newLeave)
                    showRequestDialog = false
                    Toast.makeText(context, "درخواست مرخصی با موفقیت ثبت شد", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
fun RequestLeaveDialog(
    employees: List<EmployeeEntity>,
    onDismiss: () -> Unit,
    onSubmit: (LeaveRecordEntity) -> Unit
) {
    val theme = LocalJaynoTheme.current

    var selectedEmpId by remember { mutableStateOf(employees.firstOrNull()?.id ?: "") }
    var leaveType by remember { mutableStateOf("DAILY") }
    var startDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var endDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var startTime by remember { mutableStateOf("10:00") }
    var endTime by remember { mutableStateOf("12:00") }
    var reason by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = theme.cardBackground,
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("request_leave_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "ثبت مرخصی پرسنل",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = theme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Employee selector chips
                Text("انتخاب پرسنل:", fontSize = 11.sp, color = theme.onSurfaceVariant)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(employees) { e ->
                        val isSel = selectedEmpId == e.id
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) theme.primary else theme.surfaceVariant,
                            modifier = Modifier.clickable { selectedEmpId = e.id }
                        ) {
                            Text(
                                text = e.fullName,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) theme.onPrimary else theme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Leave Type chips
                Text("نوع مرخصی:", fontSize = 11.sp, color = theme.onSurfaceVariant)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val types = listOf("DAILY" to "روزانه", "HOURLY" to "ساعتی", "SICK" to "استعلاجی", "MISSION" to "ماموریت", "UNPAID" to "بدون حقوق")
                    items(types) { (tKey, tLabel) ->
                        val isSel = leaveType == tKey
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) theme.primary else theme.surfaceVariant,
                            modifier = Modifier.clickable { leaveType = tKey }
                        ) {
                            Text(
                                text = tLabel,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) theme.onPrimary else theme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("تاریخ شروع (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("تاریخ پایان") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                if (leaveType == "HOURLY") {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("ساعت شروع (HH:mm)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = { Text("ساعت پایان") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("دلیل مرخصی") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("انصراف")
                    }
                    Button(
                        onClick = {
                            val entity = LeaveRecordEntity(
                                employeeId = selectedEmpId,
                                startDate = startDate,
                                endDate = endDate,
                                startTime = if (leaveType == "HOURLY") startTime else null,
                                endTime = if (leaveType == "HOURLY") endTime else null,
                                leaveType = leaveType,
                                status = "APPROVED",
                                reason = reason
                            )
                            onSubmit(entity)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                        modifier = Modifier.weight(1f).testTag("submit_leave_btn")
                    ) {
                        Text("ثبت و تایید")
                    }
                }
            }
        }
    }
}
