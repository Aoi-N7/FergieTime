package com.example.fergietime

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.fergietime.ui.theme.FergieTimeTheme
import com.example.fergietime.CacheScreen
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    // 位置情報取得・送信用のユーティリティクラス
    private lateinit var locationSensor: LocationSensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase を初期化（FirestoreやAuthを使用するために必要）
        FirebaseApp.initializeApp(this)

        // ステータスバーとナビゲーションバーの描画をアプリ領域に拡張
        enableEdgeToEdge()

        // 通知チャンネルを作成（API 26以上で必要）
        Notice.createChannel(this)

        // Android 13以上の場合、通知パーミッションの確認とリクエスト
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        // 位置情報の準備と取得
        locationSensor = LocationSensor(this)
        locationSensor.requestPermission()        // パーミッションリクエスト（初回のみ表示）
        locationSensor.requestCurrentLocation()   // 現在地の取得を開始

        // Jetpack Compose によるUIの構築
        setContent {
            FergieTimeTheme {
                // 表示中の画面を管理するステート
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Location) }

                // 画面遷移の判定とUI表示
                when (currentScreen) {
                    is Screen.Location -> LocationScreen(
                        locationSensor = locationSensor,
                        onNavigateTo = { currentScreen = it } // 他画面への遷移時に画面を更新
                    )

                    is Screen.Notification -> NotificationScreen(
                        context = this,
                        onBack = { currentScreen = Screen.Location } // 戻るボタン処理
                    )

                    is Screen.Cache -> CacheScreen(
                        onBack = { currentScreen = Screen.Location }
                    )

                    is Screen.Login -> LoginScreen(
                        onBack = { currentScreen = Screen.Location },
                        onNavigateToRegister = { currentScreen = Screen.Register }
                    )

                    is Screen.Register -> RegisterScreen(
                        onBack = { currentScreen = Screen.Login }
                    )
                }
            }
        }
    }
}
