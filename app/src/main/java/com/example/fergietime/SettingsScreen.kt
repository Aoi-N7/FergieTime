package com.example.disasterapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.disasterapp.components.SettingItem

@Composable
fun SettingsScreen(
    onSettingClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "設定",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        item {
            SettingItem(
                icon = Icons.Default.Language,
                title = "言語",
                subtitle = "日本語",
                onClick = { onSettingClick("language") }
            )
        }
        
        item {
            SettingItem(
                icon = Icons.Default.DarkMode,
                title = "テーマ",
                subtitle = "ライト ダーク 自動",
                onClick = { onSettingClick("theme") },
                isHighlighted = true
            )
        }
        
        item {
            SettingItem(
                icon = Icons.Default.VolumeUp,
                title = "音声案内",
                subtitle = null,
                onClick = { onSettingClick("voice") }
            )
        }
        
        item {
            SettingItem(
                icon = Icons.Default.Person,
                title = "ユーザー設定",
                subtitle = "自宅・勤務先・学校の登録・編集",
                onClick = { onSettingClick("user") }
            )
        }
        
        item {
            SettingItem(
                icon = Icons.Default.Group,
                title = "家族情報の登録・編集",
                subtitle = "家族の名前や地域先の登録・編集",
                onClick = { onSettingClick("family") }
            )
        }
        
        item {
            SettingItem(
                icon = Icons.Default.Notifications,
                title = "通知",
                subtitle = null,
                onClick = { onSettingClick("notifications") }
            )
        }
        
        item {
            SettingItem(
                icon = Icons.Default.Delete,
                title = "キャッシュの削除",
                subtitle = null,
                onClick = { onSettingClick("cache") }
            )
        }
        
        item {
            SettingItem(
                icon = Icons.Default.Info,
                title = "アプリバージョン",
                subtitle = null,
                onClick = { onSettingClick("version") }
            )
        }
    }
}
