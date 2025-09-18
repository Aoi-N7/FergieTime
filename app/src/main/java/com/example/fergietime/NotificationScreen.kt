//// 通知機能の確認用画面(現在未使用)
//package com.example.fergietime
//
//import android.content.Context
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.example.fergietime.Notice
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.foundation.shape.RoundedCornerShape
//
//@Composable
//fun NotificationScreen(
//    context: Context,   // 通知を送信するために必要なコンテキスト
//    onBack: () -> Unit  // 戻るボタンを押したときの処理
//) {
//    // 通知のオン・オフ状態を保持するフラグ
//    var isOn by remember { mutableStateOf(true) }
//
//    // 全体のレイアウトを縦方向に配置
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp),  // 画面の内側余白
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // 画面上部の戻るボタンとタイトル
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            IconButton(onClick = onBack) {
//                Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
//            }
//            Spacer(modifier = Modifier.width(8.dp))
//            Text("通知機能", style = MaterialTheme.typography.titleLarge)
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // オンボタン
//        Button(
//            onClick = { isOn = true }, // 押すと通知を有効化
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp),
//            shape = RoundedCornerShape(8.dp), // 角を少し丸める
//            colors = ButtonDefaults.buttonColors(
//                containerColor = if (isOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
//                contentColor = if (isOn) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
//            )
//        ) {
//            Text("オン")
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // オフボタン
//        Button(
//            onClick = { isOn = false }, // 押すと通知を無効化
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp),
//            shape = RoundedCornerShape(8.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = if (!isOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
//                contentColor = if (!isOn) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
//            )
//        ) {
//            Text("オフ")
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // 通知を送信するボタン
//        Button(
//            onClick = {
//                // 通知がオンのときのみ通知を送信
//                if (isOn) {
//                    Notice.sendNotification(context)
//                }
//            },
//            modifier = Modifier
//                .width(300.dp)
//                .height(56.dp),
//            shape = RoundedCornerShape(8.dp)
//        ) {
//            Text("通知を送る")
//        }
//    }
//}
