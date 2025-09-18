// 避難所へのナビゲーション、天気警報、家族の位置、自分の安否状況を表示する
package com.example.fergietime

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onNavigateToEvacuation: () -> Unit,
    viewModel: SafetyStatusViewModel
) {
    // 縦スクロール可能なリスト
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // 一番近い避難所までのARナビ(予定)
            Button(
                onClick = onNavigateToEvacuation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "逃げる方向を見る",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null
                )
            }
        }

        item {
            // 天気警報カード（例：台風・大雨など）
            WeatherAlertCard()
        }

        item {
            // 家族の位置情報カード
            FamilyLocationCard()
        }

        item {
            // 自分の安否状況カード
            SafetyStatusCard(viewModel)
        }
    }
}

@Composable
fun SafetyStatusCard(viewModel: SafetyStatusViewModel) {
    // 安否状況を表示するカード
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // タイトル
            Text(
                text = "自分の安否状況",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 状態選択ボタン（安全・避難中・危険など）
            SafetyStatusButtons(viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            // 登録状況の表示（登録済みかどうか）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (viewModel.isRegistered) "登録済み" else "自分の状況を共有しよう",
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // コメント入力欄（自由記述）
            CommentInputSection(viewModel)
        }
    }
}

