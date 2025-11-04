package com.example.fergietime

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.example.fergietime.ui.theme.FergieTimeTheme

class MainActivity : ComponentActivity() {

    // 位置情報センサーの初期化（後で使用）
    lateinit var locationSensor: LocationSensor

    // 言語設定を適用する（多言語対応）
    override fun attachBaseContext(newBase: Context) {
        val context = LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase))
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase 初期化（Composeプロジェクトでは必須）
        FirebaseApp.initializeApp(this)

        // 通知や他の初期化処理
        initializeAppLogic(this)

        setContent {
            FergieTimeTheme {
                val navController = rememberNavController()
                val isLoggedIn = remember { mutableStateOf<Boolean?>(null) }

                // FirebaseAuth からログイン状態を取得
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
                        DisasterApp()
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
