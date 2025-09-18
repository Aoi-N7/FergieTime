package com.example.fergietime

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar {
        val items = listOf(
            BottomNavItem("home", "ホーム", Icons.Default.Home),
            BottomNavItem("map", "地図", Icons.Default.Map),
            BottomNavItem("safety", "安否", Icons.Default.Notifications),
            BottomNavItem("settings", "設定", Icons.Default.Settings)
        )
        
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onTabSelected(item.route) }
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
