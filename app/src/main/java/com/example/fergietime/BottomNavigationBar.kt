/**
 * アプリ下部に表示されるボトムナビゲーションバーを定義するファイル。
 *
 * Compose の NavigationBar を使用して、ホーム・地図・安否・設定の
 * 4 つのタブを表示し、現在選択中のルートに応じて UI を切り替える。
 *
 * BottomNavigationBar はルート（画面）選択時にコールバックを呼び出し、
 * ナビゲーション制御を行う役割を持つ。
 */

package com.example.fergietime

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector

// ボトムナビゲーションバーを表示する Composable
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

// ボトムナビゲーションの各タブの情報をまとめたデータクラス
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
