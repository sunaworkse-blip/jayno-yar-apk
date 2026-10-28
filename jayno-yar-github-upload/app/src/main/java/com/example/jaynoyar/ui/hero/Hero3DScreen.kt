package com.example.jaynoyar.ui.hero

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.jaynoyar.calendar.DateUtils
import com.example.jaynoyar.calendar.JalaliDate
import com.example.jaynoyar.ui.theme.LocalJaynoTheme

data class PillarItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun Hero3DScreen(
    onNavigateToAttendance: () -> Unit,
    onNavigateToSpreadsheet: () -> Unit,
    onNavigateToEmployees: () -> Unit,
    onNavigateToReports: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalJaynoTheme.current
    var reducedMotion by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableIntStateOf(0) }

    val pillars = remember {
        listOf(
            PillarItem(
                title = "محاسبات ریاضی دقیق و بدون تقریب",
                description = "محاسبه بر مبنای دقایق صحیح جهت ممانعت کامل از خطاهای محاسباتی اعشاری. تفکیک قطعی تاخیر و تعجیل از اضافه‌کار تایید شده.",
                icon = Icons.Default.Calculate,
                accentColor = Color(0xFFF59E0B)
            ),
            PillarItem(
                title = "دو حالت کاری استاندارد و اکسل‌نما",
                description = "ورود داده فرمی با جزئیات کامل یا محیط جدولی پرتراکم با ویرایش سلولی آنی، اعتبارسنجی زنده و ذخیره دسته‌ای.",
                icon = Icons.Default.TableChart,
                accentColor = Color(0xFF38BDF8)
            ),
            PillarItem(
                title = "پشتیبانی بومی از اکسل (.xlsx) و تقویم شمسی",
                description = "تولید و خوانش فایل‌های واقعی OpenXML بدون اتکا به وب، با حفظ کامل فونت‌های فارسی، فرمول‌بندی ایمن و اعتبارسنجی سطر به سطر.",
                icon = Icons.Default.Download,
                accentColor = Color(0xFF10B981)
            ),
            PillarItem(
                title = "مدیریت جامع شیفت و مرخصی‌ها",
                description = "تنظیم ساعت شناور، شیفت‌های شبانه فراتر از نیمه‌شب، پنج‌شنبه‌های نیمه‌وقت، مرخصی ساعتی/روزانه و رهگیری استراحت‌های پرداخت‌شده.",
                icon = Icons.Default.DateRange,
                accentColor = Color(0xFFA855F7)
            ),
            PillarItem(
                title = "ممیزی امن و دوازده تم بصری فاخر",
                description = "تاریخچه دست‌نخورده تغییرات پرسنلی، بایگانی پایگاه داده، و دوازده پوسته طراحی متناسب با سلیقه و نور محیط کارگاه.",
                icon = Icons.Default.Tune,
                accentColor = Color(0xFFEC4899)
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundBrush),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // === Top Hero Master Banner Section ===
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                // Background 3D Workshop Render
                Image(
                    painter = painterResource(id = R.drawable.hero_workshop_3d_1793194137781),
                    contentDescription = "کارگاه مبلمان جاینو",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay for text legibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0x70000000),
                                    Color(0x990A0E17),
                                    Color(0xFA0B0F19)
                                )
                            )
                        )
                )

                // Hero Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    // Workshop Pill Badge
                    Surface(
                        color = Color(0x3DF59E0B),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x80F59E0B)),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "کارگاه مبلمان جاینو • سامانه رسمی مدیریت تردد",
                                color = Color(0xFFFDE68A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "جاینو یار",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "مدیریت پیشرفته کارکرد پرسنل، اضافه‌کار، کسرکار و مرخصی‌ها با دقت ریاضی مطلق",
                        color = Color(0xFFE2E8F0),
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateToAttendance,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD97706),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hero_launch_attendance_btn")
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ثبت تردد امروز", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onNavigateToSpreadsheet,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFFDE68A)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFF59E0B)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hero_launch_spreadsheet_btn")
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("نمای اکسل‌نما", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // === Interactive 3D Workshop Canvas Section ===
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = theme.cardShape,
                colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
                border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = theme.cardElevation)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ViewInAr,
                                contentDescription = null,
                                tint = theme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "شبیه‌ساز ۳بعدی خط تولید کارگاه",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = theme.onBackground
                                )
                                Text(
                                    text = "برای چرخش زاویه دید، روی صحنه بکشید",
                                    fontSize = 11.sp,
                                    color = theme.onSurfaceVariant
                                )
                            }
                        }

                        // Reduced Motion Toggle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "کاهش پویانمایی",
                                fontSize = 10.sp,
                                color = theme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Switch(
                                checked = reducedMotion,
                                onCheckedChange = { reducedMotion = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = theme.primary,
                                    checkedTrackColor = theme.primaryContainer
                                ),
                                modifier = Modifier.testTag("reduced_motion_switch")
                            )
                        }
                    }

                    // Interactive 3D Canvas Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .padding(vertical = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0D121F))
                    ) {
                        Workshop3DCanvas(
                            modifier = Modifier.fillMaxSize(),
                            activeStationIndex = selectedStation,
                            reducedMotion = reducedMotion,
                            onStationSelect = { selectedStation = it }
                        )
                    }

                    // Station Navigation Chips
                    Text(
                        text = "ایستگاه‌های فعال کارگاه مبلمان جاینو:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = theme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val stations = listOf("نجاری و CNC", "اتصالات و کلاف", "رنگ و پلی‌استر", "رویه‌کوبی لمسه")
                        stations.forEachIndexed { idx, stName ->
                            val isSel = selectedStation == idx
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) theme.primaryContainer else theme.surfaceVariant,
                                border = if (isSel) androidx.compose.foundation.BorderStroke(1.dp, theme.primary) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedStation = idx }
                            ) {
                                Text(
                                    text = stName,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) theme.onPrimaryContainer else theme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // === Today's Quick Pulse Summary Bar ===
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = theme.cardShape,
                colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
                border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor)
            ) {
                val todayJalali = JalaliDate.now()
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "وضعیت زنده شیفت امروز",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = theme.onBackground
                        )
                        Text(
                            text = todayJalali.formatWithMonthName(true),
                            fontSize = 12.sp,
                            color = theme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricPill(label = "پرسنل حاضر", value = "۶ نفر", color = theme.successColor)
                        MetricPill(label = "اضافه‌کار جاری", value = "۲:۱۰ ساعت", color = theme.warningColor)
                        MetricPill(label = "مرخصی تاییدشده", value = "۱ نفر", color = theme.infoColor)
                        MetricPill(label = "تردد باز", value = "۱ نفر", color = Color(0xFFA855F7))
                    }
                }
            }
        }

        // === 5 Core Architecture Pillars ===
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = "ارکان عملکردی سامانه جاینو یار",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = theme.onBackground
                )
                Text(
                    text = "مهندسی‌شده برای پاسخگویی به چالش‌های محاسباتی و اداری کارگاه‌های مبلمان",
                    fontSize = 12.sp,
                    color = theme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                )
            }
        }

        itemsIndexed(pillars) { index, pillar ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = theme.cardShape,
                colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
                border = androidx.compose.foundation.BorderStroke(theme.cardBorderWidth, theme.cardBorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(pillar.accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = pillar.icon,
                            contentDescription = null,
                            tint = pillar.accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pillar.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = theme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pillar.description,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = theme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // === Bottom Direct Navigation Buttons ===
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateToReports,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.primaryContainer,
                        contentColor = theme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("hero_go_to_reports_btn")
                ) {
                    Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("داشبورد تحلیلی و گزارشات ماهانه", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onNavigateToEmployees,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("hero_go_to_employees_btn")
                ) {
                    Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("فهرست و پرونده پرسنل کارگاه", fontWeight = FontWeight.Bold, color = theme.onBackground)
                }
            }
        }
    }
}

@Composable
private fun MetricPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
