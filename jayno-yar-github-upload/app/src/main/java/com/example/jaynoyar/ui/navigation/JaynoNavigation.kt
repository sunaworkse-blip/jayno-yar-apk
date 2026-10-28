package com.example.jaynoyar.ui.navigation

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jaynoyar.calendar.JalaliDate
import com.example.jaynoyar.ui.theme.LocalJaynoTheme

enum class JaynoDestination(
    val titleFa: String,
    val icon: ImageVector,
    val testTag: String
) {
    HERO_3D("نمای ۳بعدی", Icons.Default.ViewInAr, "nav_hero_3d"),
    ATTENDANCE("ثبت تردد", Icons.Default.Schedule, "nav_attendance"),
    DASHBOARD("داشبورد", Icons.Default.Assessment, "nav_dashboard"),
    EMPLOYEES("پرسنل", Icons.Default.Group, "nav_employees"),
    LEAVES("مرخصی‌ها", Icons.Default.DateRange, "nav_leaves"),
    SCHEDULES("شیفت‌ها", Icons.Default.Tune, "nav_schedules"),
    SETTINGS("تنظیمات", Icons.Default.Settings, "nav_settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JaynoTopBar(
    currentDestination: JaynoDestination,
    onNavigateToSchedules: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val theme = LocalJaynoTheme.current
    val jDate = JalaliDate.now()

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ج",
                        color = theme.onPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "جاینو یار",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = theme.onBackground
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = theme.primaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "مبلمان جاینو",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = jDate.formatWithMonthName(true),
                        fontSize = 10.sp,
                        color = theme.onSurfaceVariant
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onNavigateToSchedules,
                modifier = Modifier.testTag("top_bar_schedules_btn")
            ) {
                Icon(
                    Icons.Default.Tune,
                    contentDescription = "شیفت و قوانین کارگاه",
                    tint = if (currentDestination == JaynoDestination.SCHEDULES) theme.primary else theme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.testTag("top_bar_settings_btn")
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "تنظیمات کارگاه",
                    tint = if (currentDestination == JaynoDestination.SETTINGS) theme.primary else theme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = theme.cardBackground
        )
    )
}

@Composable
fun JaynoBottomNav(
    currentDestination: JaynoDestination,
    onDestinationSelected: (JaynoDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalJaynoTheme.current

    val primaryDestinations = listOf(
        JaynoDestination.HERO_3D,
        JaynoDestination.ATTENDANCE,
        JaynoDestination.DASHBOARD,
        JaynoDestination.EMPLOYEES,
        JaynoDestination.LEAVES
    )

    NavigationBar(
        modifier = modifier
            .navigationBarsPadding()
            .testTag("main_navigation_bar"),
        containerColor = theme.cardBackground,
        tonalElevation = 6.dp
    ) {
        primaryDestinations.forEach { destination ->
            val isSelected = currentDestination == destination

            NavigationBarItem(
                selected = isSelected,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.titleFa,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = destination.titleFa,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = theme.onPrimary,
                    selectedTextColor = theme.primary,
                    indicatorColor = theme.primary,
                    unselectedIconColor = theme.onSurfaceVariant,
                    unselectedTextColor = theme.onSurfaceVariant
                ),
                modifier = Modifier.testTag(destination.testTag)
            )
        }
    }
}
