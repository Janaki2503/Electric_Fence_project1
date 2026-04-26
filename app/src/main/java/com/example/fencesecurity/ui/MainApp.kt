package com.example.fencesecurity.ui

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fencesecurity.data.remote.RetrofitClient
import com.example.fencesecurity.data.repository.FenceRepository
import com.example.fencesecurity.navigation.Screen
import com.example.fencesecurity.service.AlertPollingService
import com.example.fencesecurity.ui.screens.*
import com.example.fencesecurity.ui.viewmodel.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val repository = remember { FenceRepository(RetrofitClient.supabaseService, RetrofitClient.aiService) }
    val factory = remember { ViewModelFactory(repository) }
    val context = LocalContext.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val drawerItems = listOf(
        Screen.Dashboard,
        Screen.Alerts,
        Screen.Analytics,
        Screen.History,
        Screen.Location,
        Screen.Profile,
        Screen.Settings
    )

    val bottomNavItems = listOf(
        Screen.Dashboard,
        Screen.Alerts,
        Screen.Profile
    )

    val showBars = currentRoute != Screen.Login.route

    // Start Polling Service on Login
    LaunchedEffect(showBars) {
        if (showBars) {
            val intent = Intent(context, AlertPollingService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showBars,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Fence Security",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider()
                drawerItems.forEach { screen ->
                    NavigationDrawerItem(
                        icon = { screen.icon?.let { Icon(it, contentDescription = null) } },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (showBars) {
                    val title = drawerItems.find { it.route == currentRoute }?.title ?: "Fence Security"
                    TopAppBar(
                        title = { Text(title) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { /* Refresh logic if needed */ }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (showBars) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        bottomNavItems.forEach { screen ->
                            NavigationBarItem(
                                icon = { screen.icon?.let { Icon(it, contentDescription = null) } },
                                label = { Text(screen.title) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Login.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    })
                }
                composable(Screen.Dashboard.route) {
                    val viewModel: DashboardViewModel = viewModel(factory = factory)
                    DashboardScreen(viewModel)
                }
                composable(Screen.Alerts.route) {
                    val viewModel: AlertsViewModel = viewModel(factory = factory)
                    AlertsScreen(viewModel)
                }
                composable(Screen.Analytics.route) {
                    val viewModel: AnalyticsViewModel = viewModel(factory = factory)
                    AnalyticsScreen(viewModel)
                }
                composable(Screen.History.route) {
                    val viewModel: HistoryViewModel = viewModel(factory = factory)
                    HistoryScreen(viewModel)
                }
                composable(Screen.Location.route) {
                    LocationScreen()
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    })
                }
                composable(Screen.Settings.route) {
                    SettingsScreen()
                }
                composable(Screen.Control.route) {
                    val viewModel: ControlViewModel = viewModel(factory = factory)
                    ControlScreen(viewModel)
                }
            }
        }
    }
}
