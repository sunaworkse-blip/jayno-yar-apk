package com.example.jaynoyar.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.jaynoyar.data.WorkshopRepository
import com.example.jaynoyar.data.model.AuditLogEntity
import com.example.jaynoyar.ui.theme.AppThemeKey
import com.example.jaynoyar.ui.theme.LocalJaynoTheme
import com.example.jaynoyar.ui.theme.TwelveThemes
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    currentThemeKey: AppThemeKey,
    onSelectTheme: (AppThemeKey) -> Unit,
    usePersianDigits: Boolean,
    onTogglePersianDigits: (Boolean) -> Unit,
    repository: WorkshopRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val theme = LocalJaynoTheme.current

    var showAuditDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var backupJsonString by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundBrush)
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Workshop Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = theme.cardShape,
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(theme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = theme.primary, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "کارگاه مبلمان جاینو",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = theme.onBackground
                        )
                        Text(
                            text = "سامانه رسمی جاینو یار • نسخه ۱.۰ تولیدی",
                            fontSize = 11.sp,
                            color = theme.primary
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = theme.cardBorderColor)

                Text("مدیریت کارگاه: مهندس کریمی", fontSize = 12.sp, color = theme.onSurfaceVariant)
                Text("محل استقرار: تهران، شهرک صنعتی چهاردانگه، خیابان ۲۴، پلاک ۱۸", fontSize = 11.sp, color = theme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                Text("فعالیت تخصصی: طراحی و ساخت مبلمان استیل، راحتی، رنگ پولیش و رویه‌کوبی چرم", fontSize = 11.sp, color = theme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // === 12 Visual Themes Selector ===
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = theme.cardShape,
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = theme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "انتخاب از ۱۲ پوسته و تم گرافیکی فاخر",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = theme.onBackground
                        )
                    }
                }

                Text(
                    text = "هر تم دارای ساختار هماهنگ رنگ، حاشیه‌ها، سایه و بافت پس‌زمینه اختصاصی است:",
                    fontSize = 11.sp,
                    color = theme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                // 12 Themes Grid (2 columns)
                val allThemes = AppThemeKey.values()
                allThemes.toList().chunked(2).forEach { rowKeys ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowKeys.forEach { key ->
                            val tDef = TwelveThemes.getTheme(key)
                            val isSelected = currentThemeKey == key

                            Surface(
                                shape = tDef.cardShape,
                                color = tDef.cardBackground,
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) theme.primary else tDef.cardBorderColor
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectTheme(key) }
                                    .testTag("theme_btn_${key.name}")
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(tDef.primary)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(tDef.accentPillColor)
                                            )
                                        }

                                        if (isSelected) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "فعال",
                                                tint = theme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = key.titleFa,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = tDef.onBackground,
                                        maxLines = 1
                                    )

                                    Text(
                                        text = key.subtitleFa,
                                        fontSize = 9.sp,
                                        color = tDef.onSurfaceVariant,
                                        maxLines = 1,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Display & Number Formatting
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = theme.cardShape,
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "تنظیمات تقویم و ارقام",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = theme.onBackground
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("قالب نمایش ارقام و اعداد:", fontSize = 12.sp, color = theme.onBackground)
                        Text("نمایش با اعداد فارسی (۱۲۳) یا لاتین (123)", fontSize = 10.sp, color = theme.onSurfaceVariant)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (usePersianDigits) theme.primary else theme.surfaceVariant,
                            modifier = Modifier.clickable { onTogglePersianDigits(true) }
                        ) {
                            Text(
                                text = "فارسی (۱۲۳)",
                                fontSize = 11.sp,
                                fontWeight = if (usePersianDigits) FontWeight.Bold else FontWeight.Normal,
                                color = if (usePersianDigits) theme.onPrimary else theme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (!usePersianDigits) theme.primary else theme.surfaceVariant,
                            modifier = Modifier.clickable { onTogglePersianDigits(false) }
                        ) {
                            Text(
                                text = "لاتین (123)",
                                fontSize = 11.sp,
                                fontWeight = if (!usePersianDigits) FontWeight.Bold else FontWeight.Normal,
                                color = if (!usePersianDigits) theme.onPrimary else theme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Audit & History Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = theme.cardShape,
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("تاریخچه ممیزی و رویدادهای سیستمی", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = theme.onBackground)
                        Text("رهگیری تمامی تغییرات ثبت‌شده در ترددها، پرسنل و مرخصی‌ها", fontSize = 10.sp, color = theme.onSurfaceVariant)
                    }

                    OutlinedButton(
                        onClick = { showAuditDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("view_audit_logs_btn")
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp), tint = theme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مشاهده لاگ‌ها", fontSize = 11.sp, color = theme.primary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Backup & Restore Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = theme.cardShape,
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "پشتیبان‌گیری و بازیابی پایگاه داده (JSON)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = theme.onBackground
                )
                Text(
                    text = "تضمین بقای داده‌ها و امکان انتقال بین دستگاه‌ها بدون وابستگی به سرور خارجی",
                    fontSize = 11.sp,
                    color = theme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Export Backup
                    Button(
                        onClick = {
                            scope.launch {
                                val json = repository.exportDatabaseBackupJson()
                                backupJsonString = json
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("JaynoYarBackup", json)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "فایل پشتیبان در کلیپ‌بورد کپی شد", Toast.LENGTH_SHORT).show()

                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "پشتیبان جاینو یار - کارگاه مبلمان جاینو")
                                    putExtra(Intent.EXTRA_TEXT, json)
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "ارسال و ذخیره فایل پشتیبان JSON"))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("export_backup_btn")
                    ) {
                        Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("دریافت فایل پشتیبان", fontSize = 11.sp)
                    }

                    // Restore Backup
                    OutlinedButton(
                        onClick = { showRestoreDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("restore_backup_btn")
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp), tint = theme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("بازیابی از فایل", fontSize = 11.sp, color = theme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Reset to Seed
                OutlinedButton(
                    onClick = { showResetConfirmDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = theme.errorColor)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("بازنشانی به داده‌های نمونه کارگاه مبلمان جاینو", fontSize = 11.sp, color = theme.errorColor)
                }
            }
        }
    }

    // Audit Log Dialog
    if (showAuditDialog) {
        val allAudits by repository.recentAuditLogs.collectAsState(initial = emptyList())
        AuditLogsDialog(audits = allAudits, onDismiss = { showAuditDialog = false })
    }

    // Restore Database Dialog
    if (showRestoreDialog) {
        RestoreDatabaseDialog(
            onDismiss = { showRestoreDialog = false },
            onRestore = { json ->
                scope.launch {
                    val success = repository.restoreDatabaseFromJson(json)
                    if (success) {
                        Toast.makeText(context, "پایگاه داده با موفقیت بازیابی شد", Toast.LENGTH_LONG).show()
                        showRestoreDialog = false
                    } else {
                        Toast.makeText(context, "خطا در قالب فایل پشتیبان JSON", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    // Reset Confirmation Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("بازنشانی به داده‌های اولیه کارگاه") },
            text = { Text("آیا مطمئن هستید؟ تمامی داده‌ها به حالت اولیه کارگاه مبلمان جاینو برگردانده خواهند شد.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            repository.resetToWorkshopDemoData()
                            showResetConfirmDialog = false
                            Toast.makeText(context, "داده‌های کارگاه بازنشانی شد", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.errorColor)
                ) {
                    Text("بله، بازنشانی شود")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}

@Composable
fun AuditLogsDialog(audits: List<AuditLogEntity>, onDismiss: () -> Unit) {
    val theme = LocalJaynoTheme.current
    val sdf = remember { SimpleDateFormat("HH:mm - yyyy/MM/dd", Locale.getDefault()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = theme.cardBackground,
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("دفتر ثبت وقایع و ممیزی سیستم", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = theme.onBackground)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "بستن")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(modifier = Modifier.height(350.dp)) {
                    items(audits) { a ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(theme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(a.actionType, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = theme.primary)
                                Text(sdf.format(Date(a.timestamp)), fontSize = 10.sp, color = theme.onSurfaceVariant)
                            }
                            Text(a.description, fontSize = 11.sp, color = theme.onBackground, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RestoreDatabaseDialog(onDismiss: () -> Unit, onRestore: (String) -> Unit) {
    val theme = LocalJaynoTheme.current
    var jsonText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = theme.cardBackground,
            border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("بازیابی پایگاه داده از متن JSON", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = theme.onBackground)
                Text("متن فایل پشتیبان را در کادر زیر وارد کنید:", fontSize = 11.sp, color = theme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))

                OutlinedTextField(
                    value = jsonText,
                    onValueChange = { jsonText = it },
                    placeholder = { Text("محتوای JSON پشتیبان...") },
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    maxLines = 10
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("انصراف")
                    }
                    Button(
                        onClick = { onRestore(jsonText.trim()) },
                        enabled = jsonText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("بازیابی داده‌ها")
                    }
                }
            }
        }
    }
}
