package com.example.fergietime

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

@Composable
fun DisasterApp() {
    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current

    // ▼ 保存された言語タグを監視
    val savedLang by LanguageManager.languageFlow(context).collectAsState(initial = "")

    // ▼ 保存済み言語を起動時に自動適用
    LaunchedEffect(savedLang) {
        if (savedLang.isNotBlank()) {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(savedLang)
            )
        }
    }

    // ▼ アプリ全体のテーマとナビゲーション構成
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = "settings"
            ) {
                // 設定一覧
                composable("settings") {
                    SettingsScreen(onSettingClick = { selectedId ->
                        navController.navigate("detail/$selectedId")
                    })
                }

                // 設定詳細（言語設定など）
                composable("detail/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")
                    SettingDetailScreen(
                        selectedSettingId = id,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
