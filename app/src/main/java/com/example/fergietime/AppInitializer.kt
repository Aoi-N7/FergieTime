/**
 * アプリ起動時に必要な初期処理をまとめたファイル。
 *
 * Firebase の初期化、通知チャンネル作成、通知権限の要求、
 * 位置情報センサーの初期化および現在地取得を行う。
 *
 * MainActivity から呼び出され、アプリが必要とする基本準備を整える役割を持つ。
 */

package com.example.fergietime

import android.os.Build
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.example.fergietime.Notice
import com.example.fergietime.LocationSensor
import androidx.activity.enableEdgeToEdge

// アプリの初期ロジックをまとめて実行する関数
fun initializeAppLogic(activity: MainActivity) {
    // Firebase を初期化
    FirebaseApp.initializeApp(activity)

    // 画面をエッジツーエッジ表示に設定
    activity.enableEdgeToEdge()

    // 通知チャンネルを作成（Android 8.0 以降で必要）
    Notice.createChannel(activity)

    // Android 13 以降は通知権限が必要なためチェックし、未許可なら要求
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activity.requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }
    }

    // 位置情報センサーを初期化
    activity.locationSensor = LocationSensor(activity)

    // 位置情報の権限を要求
    activity.locationSensor.requestPermission()

    // 現在地の取得を実行
    activity.locationSensor.requestCurrentLocation()
}
