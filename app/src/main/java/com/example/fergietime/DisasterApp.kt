package com.example.fergietime

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisasterApp() {
    val navController: NavHostController = rememberNavController()
    val viewModel: SafetyStatusViewModel = viewModel()
    val context = LocalContext.current           // ✅ Context取得
    val activity = context.findActivity()        // ✅ Activity取得（拡張関数で対応）

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

    var currentRoute by remember { mutableStateOf("home") }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onTabSelected = { route ->
                            currentRoute = route
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable("home") {
                        HomeScreen(
                            onNavigateToEvacuation = {
                                navController.navigate("evacuation")
                            },
                            viewModel = viewModel
                        )
                    }

                    composable("map") {
                        activity?.let {
                            MapScreen(
                                activity = it,
                                onPersonClick = { personId ->
                                    navController.navigate("person_detail/$personId")
                                },
                                onNavigateToEvacuation = {
                                    navController.navigate("evacuation")
                                }
                            )
                        }
                    }

                    composable("safety") {
                        SafetyScreen(
                            onPersonClick = { personId ->
                                navController.navigate("person_detail/$personId")
                            },
                            viewModel = viewModel
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            onSettingClick = { settingId ->
                                if (settingId == "user") {
                                    navController.navigate("userinfo")
                                } else {
                                    navController.navigate("setting_detail/$settingId")
                                }
                            }
                        )
                    }

                    composable("setting_detail/{settingId}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("settingId")
                        SettingDetailScreen(
                            selectedSettingId = id,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("userinfo") {
                        UserInfoScreen(onBack = { navController.popBackStack() })
                    }

                    composable("evacuation") {
                        EvacuationMapScreen(onBack = { navController.popBackStack() })
                    }

                    composable("person_detail/{personId}") {
                        PersonDetailScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}

// ✅ Context → Activity へ変換する拡張関数
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}
