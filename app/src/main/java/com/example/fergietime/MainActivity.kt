
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

// アプリのメインアクティビティ。ログイン状態に応じて画面を切り替える
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

        // Firebaseや通知などの初期化処理
        initializeAppLogic(this)

        setContent {
            FergieTimeTheme {
                val navController = rememberNavController()

                // ログイン状態を保持するステート
                val isLoggedIn = remember { mutableStateOf<Boolean?>(null) }

                // Firebaseから現在のユーザーを取得してログイン状態を判定
                LaunchedEffect(Unit) {
                    val user = FirebaseAuth.getInstance().currentUser
                    isLoggedIn.value = user != null
                }

                when (isLoggedIn.value) {
                    // ログイン状態未判定 → ローディング表示
                    null -> {
                        Surface(color = MaterialTheme.colorScheme.background) {
                            CircularProgressIndicator()
                        }
                    }
                    // ログイン済み → メイン画面（DisasterApp）へ
                    true -> {
                        DisasterApp()
                    }
                    // 未ログイン → ログイン関連画面へ
                    false -> {
                        // 画面遷移を管理するNavHostの定義
                        NavHost(navController = navController, startDestination = "login") {
                            // ログイン画面
                            composable("login") {
                                LoginScreen(
                                    onBack = {},
                                    onNavigateToRegister = { navController.navigate("register") },
                                    onNavigateToReset = { navController.navigate("reset") },
                                    onLoginSuccess = {
                                        isLoggedIn.value = true
                                    }
                                )
                            }
                            // 新規登録画面
                            composable("register") {
                                RegisterScreen(onBack = { navController.popBackStack() })
                            }
                            // パスワードリセット画面
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
