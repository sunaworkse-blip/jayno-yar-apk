package com.example.jaynoyar.ui.employees

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.jaynoyar.calendar.DateUtils
import com.example.jaynoyar.data.WorkshopRepository
import com.example.jaynoyar.data.model.EmployeeEntity
import com.example.jaynoyar.excel.XlsxEngine
import com.example.jaynoyar.ui.attendance.BadgePill
import com.example.jaynoyar.ui.theme.LocalJaynoTheme
import kotlinx.coroutines.launch

@Composable
fun EmployeesScreen(
    repository: WorkshopRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val theme = LocalJaynoTheme.current

    val allEmployees by repository.allEmployees.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedDept by remember { mutableStateOf("همه") }
    var showArchived by remember { mutableStateOf(false) }

    var editingEmployee by remember { mutableStateOf<EmployeeEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredEmployees = remember(allEmployees, searchQuery, selectedDept, showArchived) {
        allEmployees.filter { emp ->
            val matchesArchive = if (showArchived) true else emp.status != "ARCHIVED"
            val matchesDept = selectedDept == "همه" || emp.department.contains(selectedDept)
            val matchesQuery = searchQuery.isBlank() ||
                    emp.fullName.contains(searchQuery, ignoreCase = true) ||
                    emp.id.contains(searchQuery, ignoreCase = true) ||
                    emp.nationalId.contains(searchQuery)
            matchesArchive && matchesDept && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundBrush)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // Top Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "مدیریت پرسنل کارگاه",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = theme.onBackground
                )
                Text(
                    text = "${allEmployees.count { it.status == "ACTIVE" }} پرسنل فعال • کارگاه مبلمان جاینو",
                    fontSize = 11.sp,
                    color = theme.primary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Export Directory to Excel
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            try {
                                val file = XlsxEngine.exportEmployeeDirectoryXlsx(context, allEmployees)
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "خروجی اکسل فهرست پرسنل"))
                            } catch (e: Exception) {
                                Toast.makeText(context, "خطا در خروجی اکسل: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("export_employees_xlsx_btn")
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp), tint = theme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("خروجی اکسل", fontSize = 11.sp, color = theme.primary)
                }

                // Add Employee
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("add_employee_btn")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("پرسنل جدید", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("جستجوی نام، کد پرسنلی یا کد ملی...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(bottom = 6.dp)
                .testTag("search_employee_input"),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        // Department filter chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val depts = listOf("همه", "نجاری", "رنگ‌کاری", "رویه‌کوبی", "مونتاژ", "اداری")
            items(depts) { dept ->
                val isSelected = selectedDept == dept
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) theme.primary else theme.surfaceVariant,
                    modifier = Modifier.clickable { selectedDept = dept }
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

        // Employees List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredEmployees, key = { it.id }) { emp ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("employee_item_${emp.id}"),
                    shape = theme.cardShape,
                    colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
                    border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = theme.cardElevation)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(theme.primary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = emp.firstName.take(1),
                                        color = theme.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = emp.fullName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = theme.onBackground
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "(${emp.id})",
                                            fontSize = 11.sp,
                                            color = theme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "${emp.department} • ${emp.role}",
                                        fontSize = 11.sp,
                                        color = theme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                IconButton(onClick = { editingEmployee = emp }) {
                                    Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = theme.primary, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            val newStatus = if (emp.status == "ARCHIVED") "ACTIVE" else "ARCHIVED"
                                            repository.setEmployeeStatus(emp.id, newStatus)
                                            val msg = if (newStatus == "ARCHIVED") "پرسنل بایگانی شد (سوابق محفوظ است)" else "پرسنل مجدداً فعال گردید"
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Icon(
                                        if (emp.status == "ARCHIVED") Icons.Default.Refresh else Icons.Default.Archive,
                                        contentDescription = "تغییر وضعیت",
                                        tint = if (emp.status == "ARCHIVED") theme.successColor else theme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(theme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("کد ملی: ${emp.nationalId.ifBlank { "-" }}", fontSize = 11.sp, color = theme.onSurfaceVariant)
                            Text("تماس: ${emp.phone.ifBlank { "-" }}", fontSize = 11.sp, color = theme.onSurfaceVariant)
                            Text("دستمزد: ${DateUtils.toPersianDigits(emp.baseHourlyWage.toString())} ت", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = theme.onBackground)
                        }

                        if (emp.notes.isNotBlank()) {
                            Text(
                                text = "توضیحات: ${emp.notes}",
                                fontSize = 10.sp,
                                color = theme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Dialogs
    if (showAddDialog) {
        EmployeeEditDialog(
            employee = null,
            onDismiss = { showAddDialog = false },
            onSave = { newEmp ->
                scope.launch {
                    repository.saveEmployee(newEmp)
                    showAddDialog = false
                    Toast.makeText(context, "پرسنل جدید با موفقیت اضافه شد", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    editingEmployee?.let { emp ->
        EmployeeEditDialog(
            employee = emp,
            onDismiss = { editingEmployee = null },
            onSave = { updated ->
                scope.launch {
                    repository.saveEmployee(updated)
                    editingEmployee = null
                    Toast.makeText(context, "پرونده پرسنل بروزرسانی شد", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}
