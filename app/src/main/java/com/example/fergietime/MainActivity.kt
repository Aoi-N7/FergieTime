package com.example.fergietime

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import com.example.fergietime.ui.theme.FergieTimeTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    lateinit var locationSensor: LocationSensor

    override fun attachBaseContext(newBase: Context) {
        val context = LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase))
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        initializeAppLogic(this)

        setContent {

            // ▼ ① テーマ状態をアプリ全体で保持
            var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }

            // ▼ ② FergieTimeTheme に渡す
            FergieTimeTheme(themeMode = themeMode) {

                val navController = rememberNavController()
                val isLoggedIn = remember { mutableStateOf<Boolean?>(null) }

                // ログインチェック
                LaunchedEffect(Unit) {
                    val user = FirebaseAuth.getInstance().currentUser
                    isLoggedIn.value = user != null
                }

                when (isLoggedIn.value) {

                    null -> {
                        Surface(color = MaterialTheme.colorScheme.background) {
                            CircularProgressIndicator()
                        }
                    }

                    true -> {
                        // ▼ ③ メインアプリにテーマ更新関数を渡す
                        DisasterApp(
                            onThemeChanged = { mode -> themeMode = mode }
                        )
                    }

                    false -> {
                        NavHost(navController = navController, startDestination = "login") {

                            composable("login") {
                                LoginScreen(
                                    onNavigateToRegister = { navController.navigate("register") },
                                    onNavigateToReset = { navController.navigate("reset") },
                                    onLoginSuccess = { isLoggedIn.value = true }
                                )
                            }

                            composable("register") {
                                RegisterScreen(onBack = { navController.popBackStack() })
                            }

                            composable("reset") {
                                PasswordResetScreen(onBack = { navController.popBackStack() })
                            }
                        }
                    }
                }
            }
        }
    }
}
