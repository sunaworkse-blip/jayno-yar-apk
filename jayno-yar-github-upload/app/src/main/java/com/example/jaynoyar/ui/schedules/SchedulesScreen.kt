package com.example.jaynoyar.ui.schedules

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jaynoyar.data.SeedData
import com.example.jaynoyar.data.WorkshopRepository
import com.example.jaynoyar.data.model.WorkScheduleEntity
import com.example.jaynoyar.ui.theme.LocalJaynoTheme
import kotlinx.coroutines.launch

@Composable
fun SchedulesScreen(
    repository: WorkshopRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val theme = LocalJaynoTheme.current

    var schedule by remember { mutableStateOf(SeedData.DEFAULT_SCHEDULE) }
    var title by remember { mutableStateOf(schedule.title) }
    var startTime by remember { mutableStateOf(schedule.standardStartTime) }
    var endTime by remember { mutableStateOf(schedule.standardEndTime) }
    var dailyMinutes by remember { mutableStateOf(schedule.dailyExpectedMinutes.toString()) }
    var lunchBreak by remember { mutableStateOf(schedule.lunchBreakMinutes.toString()) }
    var teaBreak by remember { mutableStateOf(schedule.teaBreakMinutes.toString()) }
    var thursdayMinutes by remember { mutableStateOf(schedule.thursdayExpectedMinutes.toString()) }
    var fridayHoliday by remember { mutableStateOf(schedule.fridayIsHoliday) }

    LaunchedEffect(Unit) {
        val loaded = repository.getSchedule()
        schedule = loaded
        title = loaded.title
        startTime = loaded.standardStartTime
        endTime = loaded.standardEndTime
        dailyMinutes = loaded.dailyExpectedMinutes.toString()
        lunchBreak = loaded.lunchBreakMinutes.toString()
        teaBreak = loaded.teaBreakMinutes.toString()
        thursdayMinutes = loaded.thursdayExpectedMinutes.toString()
        fridayHoliday = loaded.fridayIsHoliday
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundBrush)
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "تنظیمات شیفت و قوانین کارکرد",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = theme.onBackground
                )
                Text(
                    text = "تعریف ساعات موظفی، استراحت، و پنج‌شنبه‌های کارگاه",
                    fontSize = 11.sp,
                    color = theme.primary
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        val updated = schedule.copy(
                            title = title.trim(),
                            standardStartTime = startTime.trim(),
                            standardEndTime = endTime.trim(),
                            dailyExpectedMinutes = dailyMinutes.toIntOrNull() ?: 480,
                            lunchBreakMinutes = lunchBreak.toIntOrNull() ?: 45,
                            teaBreakMinutes = teaBreak.toIntOrNull() ?: 15,
                            thursdayExpectedMinutes = thursdayMinutes.toIntOrNull() ?: 240,
                            fridayIsHoliday = fridayHoliday
                        )
                        repository.saveSchedule(updated)
                        Toast.makeText(context, "قوانین شیفت کاری با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_schedule_btn")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("ذخیره قوانین", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = theme.cardShape,
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان شیفت کاری کارگاه") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("شروع شیفت عادی (HH:mm)") },
                        modifier = Modifier.weight(1f).testTag("sched_start_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("پایان شیفت عادی (HH:mm)") },
                        modifier = Modifier.weight(1f).testTag("sched_end_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = dailyMinutes,
                    onValueChange = { dailyMinutes = it },
                    label = { Text("کارکرد موظف روزانه شنبه تا چهارشنبه (دقیقه - مثلاً ۴۸۰ دقیقه = ۸ ساعت)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("sched_expected_min_input"),
                    singleLine = true
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = theme.cardBorderColor)

                Text("قوانین استراحت و پذیرایی:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = theme.onBackground)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = lunchBreak,
                        onValueChange = { lunchBreak = it },
                        label = { Text("استراحت ناهار (دقیقه - کسر از کارکرد)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = teaBreak,
                        onValueChange = { teaBreak = it },
                        label = { Text("استراحت چای (دقیقه - با حقوق)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = theme.cardBorderColor)

                Text("پنج‌شنبه‌ها و روزهای پایان هفته:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = theme.onBackground)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = thursdayMinutes,
                    onValueChange = { thursdayMinutes = it },
                    label = { Text("موظفی روز پنج‌شنبه (دقیقه - مثلاً ۲۴۰ دقیقه = ۴ ساعت)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = fridayHoliday, onCheckedChange = { fridayHoliday = it })
                    Text("جمعه‌ها تعطیل رسمی هفتگی کارگاه باشد (موظفی = ۰)", fontSize = 12.sp, color = theme.onBackground)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = theme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = theme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تغییر قوانین شیفت سوابق گذشته را مخدوش نمی‌کند و برای محاسبات از تاریخ اعمال به بعد منظور می‌گردد.",
                            fontSize = 11.sp,
                            color = theme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
