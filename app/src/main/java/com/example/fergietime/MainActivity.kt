package com.example.fergietime

import android.os.Bundle
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.example.fergietime.ui.theme.FergieTimeTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*

class MainActivity : ComponentActivity() {

    // 位置情報取得用のクラス
    private lateinit var locationSensor: LocationSensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase の初期化処理
        FirebaseApp.initializeApp(this)

        // ステータスバーやナビゲーションバーの背景をアプリの内容で描画できるようにする
        enableEdgeToEdge()

        // 通知チャンネルを作成する
        Notice.createChannel(this)

        // Android 13 以降では通知の許可を明示的に求める必要がある
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        // 位置情報の権限確認と現在位置の取得を行う
        locationSensor = LocationSensor(this)
        locationSensor.requestPermission()
        locationSensor.requestCurrentLocation()

        // Jetpack Compose による画面表示の開始
        setContent {
            FergieTimeTheme {
                // 現在表示する画面を保持する状態変数
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Loading) }

                // 自動ログインをチェックする処理
                LaunchedEffect(Unit) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        // ログイン中のユーザーがいれば Firestore からユーザー名を取得
                        FirebaseFirestore.getInstance()
                            .collection("userdata")
                            .document(user.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                val name = document.getString("name")
                                if (name != null) {
                                    currentScreen = Screen.Home(name)
                                } else {
                                    currentScreen = Screen.Login
                                }
                            }
                            .addOnFailureListener {
                                currentScreen = Screen.Login
                            }
                    } else {
                        // 未ログインならログイン画面に遷移
                        currentScreen = Screen.Login
                    }
                }

                // 現在の画面の状態に応じて適切な Composable を表示する
                when (currentScreen) {
                    is Screen.Loading -> {
                        // ローディング中はインジケーターを中央に表示する
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is Screen.Location -> LocationScreen(
                        locationSensor = locationSensor,
                        onNavigateTo = { currentScreen = it }
                    )

                    is Screen.Notification -> NotificationScreen(
                        context = this,
                        onBack = { currentScreen = Screen.Location }
                    )

                    is Screen.Cache -> CacheScreen(
                        onBack = { currentScreen = Screen.Location }
                    )

                    is Screen.Login -> LoginScreen(
                        onBack = { currentScreen = Screen.Location },
                        onNavigateToRegister = { currentScreen = Screen.Register },
                        onNavigateToReset = { currentScreen = Screen.PasswordReset },
                        onLoginSuccess = { name -> currentScreen = Screen.Home(name) }
                    )

                    is Screen.Register -> RegisterScreen(
                        onBack = { currentScreen = Screen.Login }
                    )

                    is Screen.PasswordReset -> PasswordResetScreen(
                        onBack = { currentScreen = Screen.Login }
                    )

                    is Screen.Home -> HomeScreen(
                        userName = (currentScreen as Screen.Home).userName,
                        onBack = { currentScreen = Screen.Login },
                        onLogout = {
                            // サインアウト後にログイン画面へ遷移
                            FirebaseAuth.getInstance().signOut()
                            currentScreen = Screen.Login
                        }
                    )
                }
            }
        }
    }
}
