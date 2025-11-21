/**
 * 避難経路画面（EvacuationMapScreen）を構成する UI をまとめたファイル。
 *
 * 簡易的な地図表示、現在地・目的地のマーカー描画、避難ルートのステップ表示、
 * ナビゲーション開始ボタンなど、避難所までの案内 UI を提供する。
 *
 * Canvas を用いた地図の背景描画や、ルート案内カード、ボタン UI など
 * 複数のコンポーネントで構成されている。
 */

package com.example.fergietime

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
// 避難経路画面のトップレベル UI
@Composable
fun EvacuationMapScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "避難経路",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "●●小学校まで",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "戻る"
                        )
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "7分",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item { EvacuationMapContainer() }

            item { RouteInstructions() }

            item { StartNavigationButton() }
        }
    }
}

// 地図背景と現在地・目的地マーカーを描画するコンテナ
@Composable
fun EvacuationMapContainer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawEvacuationMapBackground(this)
        }

        // 現在地
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 32.dp, y = 32.dp)
        ) {
            LocationMarker(
                backgroundColor = Color(0xFF4CAF50),
                icon = Icons.Default.Navigation,
                label = "現在地"
            )
        }

        // 目的地（避難所）
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-64).dp, y = 80.dp)
        ) {
            LocationMarker(
                backgroundColor = Color(0xFFF44336),
                icon = Icons.Default.LocationOn,
                label = "●●小学校"
            )
        }
    }
}

// 避難ルートのステップ案内を表示
@Composable
fun RouteInstructions() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RouteInstructionCard(
            stepNumber = "1",
            instruction = "南に向かって直進",
            distance = "200m • 約2分",
            isActive = true
        )

        RouteInstructionCard(
            stepNumber = "2",
            instruction = "右折して東に向かう",
            distance = "150m • 約2分",
            isActive = false
        )

        RouteInstructionCard(
            stepNumber = "3",
            instruction = "左折して北に向かう",
            distance = "100m • 約1分",
            isActive = false
        )

        RouteInstructionCard(
            stepNumber = null,
            instruction = "●●小学校に到着",
            distance = "右側に避難所の入口があります",
            isActive = false,
            isDestination = true
        )
    }
}

// ナビゲーション開始ボタン
@Composable
fun StartNavigationButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ナビゲーション開始",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// キャンバス上に簡易地図（道路や避難ルート）を描画
fun drawEvacuationMapBackground(drawScope: DrawScope) {
    with(drawScope) {
        val width = size.width
        val height = size.height

        val streetColor = Color(0xFFE5E7EB)

        // 横方向の道路
        drawLine(streetColor, Offset(0f, height * 0.2f), Offset(width, height * 0.2f), strokeWidth = 4f)
        drawLine(streetColor, Offset(0f, height * 0.4f), Offset(width, height * 0.4f), strokeWidth = 6f)
        drawLine(streetColor, Offset(0f, height * 0.6f), Offset(width, height * 0.6f), strokeWidth = 4f)
        drawLine(streetColor, Offset(0f, height * 0.8f), Offset(width, height * 0.8f), strokeWidth = 4f)

        // 縦方向の道路
        drawLine(streetColor, Offset(width * 0.2f, 0f), Offset(width * 0.2f, height), strokeWidth = 4f)
        drawLine(streetColor, Offset(width * 0.4f, 0f), Offset(width * 0.4f, height), strokeWidth = 4f)
        drawLine(streetColor, Offset(width * 0.6f, 0f), Offset(width * 0.6f, height), strokeWidth = 6f)
        drawLine(streetColor, Offset(width * 0.8f, 0f), Offset(width * 0.8f, height), strokeWidth = 4f)

        // 青の点線で避難ルートを描画
        val routeColor = Color(0xFF2563EB)
        val path = Path().apply {
            moveTo(width * 0.11f, height * 0.11f)
            lineTo(width * 0.11f, height * 0.4f)
            lineTo(width * 0.5f, height * 0.4f)
            lineTo(width * 0.5f, height * 0.3f)
            lineTo(width * 0.7f, height * 0.3f)
        }

        drawPath(
            path = path,
            color = routeColor,
            style = Stroke(
                width = 8f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 8f))
            )
        )

        // ルート上のポイントを描画
        drawCircle(
            color = Color(0xFF2563EB),
            radius = 6f,
            center = Offset(width * 0.2f, width * 0.2f)
        )
        drawCircle(
            color = Color(0xFF2563EB),
            radius = 6f,
            center = Offset(width * 0.4f, width * 0.4f)
        )
        drawCircle(
            color = Color(0xFF2563EB),
            radius = 6f,
            center = Offset(width * 0.5f, width * 0.3f)
        )
    }
}
