package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.jaynoyar.data.WorkshopDatabase
import com.example.jaynoyar.data.WorkshopRepository
import com.example.jaynoyar.ui.attendance.AttendanceScreen
import com.example.jaynoyar.ui.dashboard.DashboardScreen
import com.example.jaynoyar.ui.employees.EmployeesScreen
import com.example.jaynoyar.ui.hero.Hero3DScreen
import com.example.jaynoyar.ui.leaves.LeavesScreen
import com.example.jaynoyar.ui.navigation.JaynoBottomNav
import com.example.jaynoyar.ui.navigation.JaynoDestination
import com.example.jaynoyar.ui.navigation.JaynoTopBar
import com.example.jaynoyar.ui.schedules.SchedulesScreen
import com.example.jaynoyar.ui.settings.SettingsScreen
import com.example.jaynoyar.ui.theme.AppThemeKey
import com.example.jaynoyar.ui.theme.JaynoYarTheme
import com.example.jaynoyar.ui.theme.LocalJaynoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val scope = rememberCoroutineScope()
            val database = remember { WorkshopDatabase.getDatabase(applicationContext, scope) }
            val repository = remember { WorkshopRepository(database.workshopDao()) }

            var currentThemeKey by remember { mutableStateOf(AppThemeKey.WORKSHOP_WOOD) }
            var usePersianDigits by remember { mutableStateOf(true) }
            var currentDestination by remember { mutableStateOf(JaynoDestination.HERO_3D) }

            JaynoYarTheme(themeKey = currentThemeKey) {
                val theme = LocalJaynoTheme.current

                // Handle back press: if not on Hero, return to Hero home
                BackHandler(enabled = currentDestination != JaynoDestination.HERO_3D) {
                    currentDestination = JaynoDestination.HERO_3D
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        JaynoTopBar(
                            currentDestination = currentDestination,
                            onNavigateToSchedules = { currentDestination = JaynoDestination.SCHEDULES },
                            onNavigateToSettings = { currentDestination = JaynoDestination.SETTINGS }
                        )
                    },
                    bottomBar = {
                        JaynoBottomNav(
                            currentDestination = currentDestination,
                            onDestinationSelected = { currentDestination = it }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(theme.backgroundBrush)
                    ) {
                        when (currentDestination) {
                            JaynoDestination.HERO_3D -> {
                                Hero3DScreen(
                                    onNavigateToAttendance = { currentDestination = JaynoDestination.ATTENDANCE },
                                    onNavigateToSpreadsheet = { currentDestination = JaynoDestination.ATTENDANCE },
                                    onNavigateToEmployees = { currentDestination = JaynoDestination.EMPLOYEES },
                                    onNavigateToReports = { currentDestination = JaynoDestination.DASHBOARD }
                                )
                            }

                            JaynoDestination.ATTENDANCE -> {
                                AttendanceScreen(repository = repository)
                            }

                            JaynoDestination.DASHBOARD -> {
                                DashboardScreen(
                                    repository = repository,
                                    onNavigateToAttendance = { currentDestination = JaynoDestination.ATTENDANCE }
                                )
                            }

                            JaynoDestination.EMPLOYEES -> {
                                EmployeesScreen(repository = repository)
                            }

                            JaynoDestination.LEAVES -> {
                                LeavesScreen(repository = repository)
                            }

                            JaynoDestination.SCHEDULES -> {
                                SchedulesScreen(repository = repository)
                            }

                            JaynoDestination.SETTINGS -> {
                                SettingsScreen(
                                    currentThemeKey = currentThemeKey,
                                    onSelectTheme = { currentThemeKey = it },
                                    usePersianDigits = usePersianDigits,
                                    onTogglePersianDigits = { usePersianDigits = it },
                                    repository = repository
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
