package com.example.fergietime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// テーマ設定画面
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingScreen(
    currentTheme: ThemeMode,
    onThemeChanged: (ThemeMode) -> Unit,
    onBack: () -> Unit
) {
    // ThemeMode を画面表示用の SettingOption に変換
    val themeOptions = remember {
        mutableStateListOf(
            SettingOption(
                title = "ライトテーマ",
                description = "明るい背景色を使用",
                selected = currentTheme == ThemeMode.ライト
            ),
            SettingOption(
                title = "ダークテーマ",
                description = "暗い背景色を使用",
                selected = currentTheme == ThemeMode.ダーク
            ),
            SettingOption(
                title = "自動",
                description = "システム設定に従う",
                selected = currentTheme == ThemeMode.自動
            )
        )
    }

    // 画面全体のレイアウト
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("テーマ設定", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 各テーマオプションを表示
            items(themeOptions) { option ->
                val index = themeOptions.indexOf(option)

                SettingOptionCard(
                    option = option,
                    onClick = {
                        // 選択状態を更新: クリックされた行のみ selected=true
                        val updated = themeOptions.mapIndexed { i, o ->
                            o.copy(selected = (i == index))
                        }
                        themeOptions.clear()
                        themeOptions.addAll(updated)

                        // 選択されたテーマをコールバックで通知
                        val newTheme = when (index) {
                            0 -> ThemeMode.ライト
                            1 -> ThemeMode.ダーク
                            else -> ThemeMode.自動
                        }
                        onThemeChanged(newTheme)
                    }
                )
            }
        }
    }
}
