package com.example.jaynoyar.ui.attendance

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.example.jaynoyar.calculator.AttendanceCalculator
import com.example.jaynoyar.calendar.DateUtils
import com.example.jaynoyar.calendar.JalaliDate
import com.example.jaynoyar.data.SeedData
import com.example.jaynoyar.data.WorkshopRepository
import com.example.jaynoyar.data.model.AttendanceEntity
import com.example.jaynoyar.data.model.EmployeeEntity
import com.example.jaynoyar.data.model.WorkScheduleEntity
import com.example.jaynoyar.excel.XlsxEngine
import com.example.jaynoyar.ui.theme.LocalJaynoTheme
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun AttendanceScreen(
    repository: WorkshopRepository,
    initialDate: LocalDate = LocalDate.now(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val theme = LocalJaynoTheme.current

    var selectedDate by remember { mutableStateOf(initialDate) }
    var selectedModeIndex by remember { mutableIntStateOf(0) } // 0: Standard, 1: Spreadsheet
    var searchQuery by remember { mutableStateOf("") }
    var selectedDepartment by remember { mutableStateOf("همه") }

    val employees by repository.activeEmployees.collectAsState(initial = emptyList())
    val attendancesForDay by repository.getAttendancesForDateFlow(selectedDate.toString()).collectAsState(initial = emptyList())
    var currentSchedule by remember { mutableStateOf(SeedData.DEFAULT_SCHEDULE) }

    LaunchedEffect(Unit) {
        currentSchedule = repository.getSchedule()
    }

    // Build row calculation models
    val attendanceRows = remember(employees, attendancesForDay, currentSchedule, selectedDate) {
        val attMap = attendancesForDay.associateBy { it.employeeId }
        employees.map { emp ->
            val att = attMap[emp.id] ?: AttendanceEntity(
                employeeId = emp.id,
                workDate = selectedDate.toString(),
                checkInTime = null,
                checkOutTime = null,
                breakMinutes = currentSchedule.lunchBreakMinutes
            )
            val breakdown = AttendanceCalculator.computeBreakdown(
                attendance = att,
                schedule = currentSchedule,
                isHoliday = false
            )
            EmployeeAttendanceRow(
                employee = emp,
                attendance = attMap[emp.id],
                breakdown = breakdown
            )
        }
    }

    // Filtered by department and search
    val filteredRows = remember(attendanceRows, searchQuery, selectedDepartment) {
        attendanceRows.filter { row ->
            val matchesDept = selectedDepartment == "همه" || row.employee.department.contains(selectedDepartment)
            val matchesQuery = searchQuery.isBlank() ||
                    row.employee.fullName.contains(searchQuery, ignoreCase = true) ||
                    row.employee.id.contains(searchQuery, ignoreCase = true)
            matchesDept && matchesQuery
        }
    }

    // Import Preview State
    var importResult by remember { mutableStateOf<XlsxEngine.ImportResult?>(null) }
    var isImporting by remember { mutableStateOf(false) }

    // File picker launcher for Excel .xlsx
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                isImporting = true
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        val result = XlsxEngine.parseXlsxAttendance(stream, employees)
                        importResult = result
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "خطا در خواندن فایل اکسل: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    isImporting = false
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundBrush)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // === Top Bar: Date Selector with Shamsi / Gregorian ===
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = theme.cardShape,
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor)
        ) {
            val jDate = JalaliDate.fromLocalDate(selectedDate)
            val weekdayName = DateUtils.getPersianWeekdayName(selectedDate)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Next Day (RTL: on left is forward in time)
                IconButton(
                    onClick = { selectedDate = selectedDate.plusDays(1) },
                    modifier = Modifier.testTag("next_day_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "روز بعد", tint = theme.primary)
                }

                // Date Label
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedDate = LocalDate.now() }
                ) {
                    Text(
                        text = "$weekdayName، ${jDate.formatWithMonthName(true)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = theme.onBackground
                    )
                    Text(
                        text = "میلادی: ${selectedDate.year}/${"%02d".format(selectedDate.monthValue)}/${"%02d".format(selectedDate.dayOfMonth)} (بازگشت به امروز)",
                        fontSize = 10.sp,
                        color = theme.onSurfaceVariant
                    )
                }

                // Previous Day
                IconButton(
                    onClick = { selectedDate = selectedDate.minusDays(1) },
                    modifier = Modifier.testTag("prev_day_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "روز قبل", tint = theme.primary)
                }
            }
        }

        // === Operating Mode Switcher & Excel Action Buttons ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mode Tabs
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = theme.cardBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, theme.cardBorderColor)
            ) {
                Row(modifier = Modifier.padding(3.dp)) {
                    val isStd = selectedModeIndex == 0
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isStd) theme.primary else Color.Transparent,
                        modifier = Modifier
                            .clickable { selectedModeIndex = 0 }
                            .testTag("mode_standard_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.ViewAgenda,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isStd) theme.onPrimary else theme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "حالت استاندارد",
                                fontSize = 11.sp,
                                fontWeight = if (isStd) FontWeight.Bold else FontWeight.Normal,
                                color = if (isStd) theme.onPrimary else theme.onSurfaceVariant
                            )
                        }
                    }

                    val isSheet = selectedModeIndex == 1
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSheet) theme.primary else Color.Transparent,
                        modifier = Modifier
                            .clickable { selectedModeIndex = 1 }
                            .testTag("mode_spreadsheet_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.TableChart,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSheet) theme.onPrimary else theme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "حالت اکسل‌نما",
                                fontSize = 11.sp,
                                fontWeight = if (isSheet) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSheet) theme.onPrimary else theme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Excel Actions (Export & Import)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Blank Template / Export
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            try {
                                val pairs = filteredRows.map { it.employee to it.breakdown }
                                val file = XlsxEngine.exportAttendanceReportXlsx(context, pairs, "تردد_جاینو_یار_${selectedDate}")
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "خروجی اکسل تردد کارگاه جاینو"))
                            } catch (e: Exception) {
                                Toast.makeText(context, "خطا در خروجی اکسل: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("export_xlsx_btn")
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp), tint = theme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("خروجی اکسل", fontSize = 11.sp, color = theme.primary)
                }

                // Import Excel
                Button(
                    onClick = {
                        filePickerLauncher.launch(
                            arrayOf(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                "application/octet-stream",
                                "*/*"
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primaryContainer, contentColor = theme.onPrimaryContainer),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("import_xlsx_btn")
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("بارگذاری اکسل", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search & Department Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("جستجوی نام یا کد پرسنل...", fontSize = 11.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("search_attendance_input"),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            // Download Blank Template quick button
            IconButton(
                onClick = {
                    scope.launch {
                        try {
                            val file = XlsxEngine.exportBlankTemplateXlsx(context, employees)
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "قالب خام اکسل جاینو یار"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "خطا در دانلود قالب: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            ) {
                Icon(Icons.Default.Download, contentDescription = "قالب خام اکسل", tint = theme.primary)
            }
        }

        // Department Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val depts = listOf("همه", "نجاری", "رنگ‌کاری", "رویه‌کوبی", "مونتاژ", "اداری")
            items(depts) { dept ->
                val isSelected = selectedDepartment == dept
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) theme.primary else theme.surfaceVariant,
                    modifier = Modifier.clickable { selectedDepartment = dept }
                ) {
                    Text(
                        text = dept,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) theme.onPrimary else theme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Content: Standard View vs Spreadsheet View
        if (selectedModeIndex == 0) {
            StandardAttendanceView(
                rows = filteredRows,
                onSaveAttendance = { updated ->
                    scope.launch {
                        repository.recordAttendance(updated)
                        Toast.makeText(context, "تردد با موفقیت در پایگاه داده ثبت شد", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f)
            )
        } else {
            SpreadsheetView(
                rows = filteredRows,
                schedule = currentSchedule,
                selectedDate = selectedDate.toString(),
                onSaveBatch = { attendances ->
                    scope.launch {
                        repository.saveAttendances(
                            attendances,
                            "ذخیره دسته‌ای تردد ${attendances.size} پرسنل در تاریخ $selectedDate از نمای اکسل‌نما"
                        )
                        Toast.makeText(context, "تغییرات تمامی ردیف‌ها با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }

    // Excel Import Preview Modal
    importResult?.let { result ->
        ImportPreviewDialog(
            result = result,
            onDismiss = { importResult = null },
            onCommit = {
                scope.launch {
                    val attendancesToCommit = result.validRows.map { row ->
                        AttendanceEntity(
                            employeeId = row.employeeId,
                            workDate = row.dateString,
                            checkInTime = row.checkInTime,
                            checkOutTime = row.checkOutTime,
                            breakMinutes = row.breakMinutes,
                            isBreakPaid = false,
                            overtimeApproved = true,
                            note = "ورود از فایل اکسل (ردیف ${row.rowNumber})"
                        )
                    }
                    repository.saveAttendances(
                        attendancesToCommit,
                        "ورود دسته‌ای ${attendancesToCommit.size} رکورد تردد از فایل اکسل"
                    )
                    Toast.makeText(context, "${attendancesToCommit.size} رکورد با موفقیت وارد پایگاه داده شد", Toast.LENGTH_LONG).show()
                    importResult = null
                }
            }
        )
    }
}

@Composable
fun ImportPreviewDialog(
    result: XlsxEngine.ImportResult,
    onDismiss: () -> Unit,
    onCommit: () -> Unit
) {
    val theme = LocalJaynoTheme.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = theme.cardBackground,
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("excel_import_preview_dialog")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "پیش‌نمایش و اعتبارسنجی ورود از اکسل",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = theme.onBackground
                        )
                        Text(
                            text = "${result.validRows.size} رکورد معتبر • ${result.invalidRows.size} نامعتبر • ${result.duplicateRows.size} تکراری",
                            fontSize = 11.sp,
                            color = theme.primary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "بستن")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(modifier = Modifier.height(280.dp)) {
                    // Valid rows
                    items(result.validRows) { r ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .background(theme.successColor.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = theme.successColor, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ردیف ${r.rowNumber}: ${r.employeeName ?: r.employeeId}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.onBackground)
                            }
                            Text("${r.checkInTime ?: "-"} تا ${r.checkOutTime ?: "-"}", fontSize = 11.sp, color = theme.onSurfaceVariant)
                        }
                    }

                    // Invalid rows
                    items(result.invalidRows) { r ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .background(theme.errorColor.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = theme.errorColor, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ردیف ${r.rowNumber}: ${r.employeeId}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.errorColor)
                            }
                            Text(r.errorMessage ?: "خطا", fontSize = 10.sp, color = theme.errorColor)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("انصراف")
                    }

                    Button(
                        onClick = onCommit,
                        enabled = result.validRows.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("commit_import_btn")
                    ) {
                        Text("تایید و ثبت نهایی (${result.validRows.size})")
                    }
                }
            }
        }
    }
}
