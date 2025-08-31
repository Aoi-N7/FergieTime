package com.example.fergietime

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat

object Notice {
    // 通知チャンネルID（一意な識別子）
    private const val CHANNEL_ID = "custom_channel_id"
    private const val NOTIFICATION_ID = 1  // 通知ID（同じIDなら上書きされる）

    // 通知チャンネルの作成（Android 8.0 以降が対象）
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 通知音として res/raw/sound.mp3 を指定
            val soundUri = Uri.parse("android.resource://${context.packageName}/${R.raw.sound}")

            // 通知音の属性（用途・種別など）
            val attributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) // 音声タイプ：効果音
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)             // 用途：通知
                .build()

            // 通知チャンネルの設定
            val channel = NotificationChannel(
                CHANNEL_ID,
                "通知チャンネル",  // ユーザーに表示されるチャンネル名
                NotificationManager.IMPORTANCE_HIGH  // 通知の重要度（大）
            ).apply {
                description = "通知バナーとカスタム音のチャンネル"
                enableVibration(true)  // バイブレーション有効
                setSound(soundUri, attributes)  // カスタム音を設定
            }

            // システムにチャンネルを登録
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    // 通知を送信する処理
    fun sendNotification(context: Context) {
        // Android 13 (API 33) 以降は通知の権限が必要
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            )

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.w("Notice", "通知権限が許可されていません")
                return  // 権限がなければ通知を出さない
            }
        }

        // 通知をタップしたときに起動する画面（MainActivity）
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE  // 不変な PendingIntent にする必要あり（APIレベル31以降推奨）
        )

        // 通知の内容を設定
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)  // 通知に表示するアイコン
            .setContentTitle("通知が届きました")              // タイトル
            .setContentText("これはカスタム通知音＋バナーの通知です。")  // 本文
            .setPriority(NotificationCompat.PRIORITY_HIGH)    // 優先度（即時表示）
            .setContentIntent(pendingIntent)                  // タップ時の動作
            .setAutoCancel(true)                              // タップで通知を消す

        // 通知を送信
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }
}
