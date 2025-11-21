package com.example.fergietime

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 天気警報カード
 *
 * 大雨や災害時の注意喚起を表示するカードコンポーネント
 */
@Composable
fun WeatherAlertCard() {
    // カード本体
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)) // 薄いオレンジ背景
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // アイコン部分
            Card(
                modifier = Modifier.size(48.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800)), // オレンジ背景
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "警告アイコン",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 警報テキスト
            Column {
                Text(
                    text = "大雨警報", // 警報タイトル
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
                Text(
                    text = "12時30分 現在 神戸市全域", // 発表時間・地域
                    fontSize = 14.sp,
                    color = Color(0xFFBF360C)
                )
                Text(
                    text = "非常に激しい雨（50mm/h）", // 詳細情報
                    fontSize = 12.sp,
                    color = Color(0xFF8D6E63)
                )
            }
        }
    }
}
