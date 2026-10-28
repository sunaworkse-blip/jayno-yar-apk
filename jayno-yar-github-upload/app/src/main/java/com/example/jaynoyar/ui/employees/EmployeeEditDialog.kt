package com.example.jaynoyar.ui.employees

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.jaynoyar.data.model.EmployeeEntity
import com.example.jaynoyar.ui.theme.LocalJaynoTheme
import java.time.LocalDate

@Composable
fun EmployeeEditDialog(
    employee: EmployeeEntity?,
    onDismiss: () -> Unit,
    onSave: (EmployeeEntity) -> Unit
) {
    val theme = LocalJaynoTheme.current

    var id by remember { mutableStateOf(employee?.id ?: "EMP-${(100..999).random()}") }
    var firstName by remember { mutableStateOf(employee?.firstName ?: "") }
    var lastName by remember { mutableStateOf(employee?.lastName ?: "") }
    var department by remember { mutableStateOf(employee?.department ?: "نجاری و ساخت") }
    var role by remember { mutableStateOf(employee?.role ?: "استادکار") }
    var nationalId by remember { mutableStateOf(employee?.nationalId ?: "") }
    var phone by remember { mutableStateOf(employee?.phone ?: "") }
    var hireDate by remember { mutableStateOf(employee?.hireDate ?: LocalDate.now().toString()) }
    var wage by remember { mutableStateOf((employee?.baseHourlyWage ?: 120000).toString()) }
    var notes by remember { mutableStateOf(employee?.notes ?: "") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = theme.cardBackground,
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("employee_edit_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (employee == null) "افزودن پرسنل جدید به کارگاه" else "ویرایش پرونده پرسنل",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = theme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("کد پرسنلی (یکتا)") },
                    enabled = employee == null,
                    modifier = Modifier.fillMaxWidth().testTag("emp_id_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("نام") },
                        modifier = Modifier.weight(1f).testTag("emp_first_name_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("نام خانوادگی") },
                        modifier = Modifier.weight(1f).testTag("emp_last_name_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("بخش کارگاه") },
                        modifier = Modifier.weight(1f).testTag("emp_dept_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("سمت / تخصص") },
                        modifier = Modifier.weight(1f).testTag("emp_role_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = nationalId,
                        onValueChange = { nationalId = it },
                        label = { Text("کد ملی") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("emp_nid_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("تلفن همراه") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f).testTag("emp_phone_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = hireDate,
                        onValueChange = { hireDate = it },
                        label = { Text("تاریخ استخدام (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = wage,
                        onValueChange = { wage = it },
                        label = { Text("دستمزد ساعتی (تومان)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("یادداشت و مهارت‌ها") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = theme.errorColor,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

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
                            if (firstName.isBlank() || lastName.isBlank()) {
                                errorMessage = "نام و نام خانوادگی نباید خالی باشد"
                                return@Button
                            }
                            val updated = EmployeeEntity(
                                id = id.trim().uppercase(),
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                department = department.trim(),
                                role = role.trim(),
                                nationalId = nationalId.trim(),
                                phone = phone.trim(),
                                hireDate = hireDate.trim(),
                                status = employee?.status ?: "ACTIVE",
                                baseHourlyWage = wage.toLongOrNull() ?: 100000,
                                notes = notes.trim()
                            )
                            onSave(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                        modifier = Modifier.weight(1f).testTag("save_employee_btn")
                    ) {
                        Text("ذخیره پرونده")
                    }
                }
            }
        }
    }
}
