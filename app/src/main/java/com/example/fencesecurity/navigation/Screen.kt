package com.example.fencesecurity.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Login : Screen("login", "Login")
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Alerts : Screen("alerts", "Alerts", Icons.Default.Notifications)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.Analytics)
    object History : Screen("history", "History", Icons.Default.History)
    object Location : Screen("location", "Location", Icons.Default.LocationOn)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Control : Screen("control", "Control", Icons.Default.Settings)
}
