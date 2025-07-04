package com.example.fergietime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

class ArNavigationActivity : ComponentActivity() {

    private var shelterName: String = ""
    private var shelterLat: Double = 0.0
    private var shelterLng: Double = 0.0
    private var userLat: Double = 0.0
    private var userLng: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // インテントからデータを取得
        shelterName = intent.getStringExtra("shelter_name") ?: ""
        shelterLat = intent.getDoubleExtra("shelter_lat", 0.0)
        shelterLng = intent.getDoubleExtra("shelter_lng", 0.0)
        userLat = intent.getDoubleExtra("user_lat", 0.0)
        userLng = intent.getDoubleExtra("user_lng", 0.0)

        setContent {
            MaterialTheme {
                ArNavigationScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ArNavigationScreen() {
        var distance by remember { mutableStateOf(calculateDistance()) }
        var bearing by remember { mutableStateOf(calculateBearing()) }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ヘッダー
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ARナビゲーション",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = shelterName,
                            fontSize = 14.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE53E3E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )

            // ARビュー（プレースホルダー）
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // ARカメラビューのプレースホルダー
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📱 ARカメラビュー",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "カメラを避難所の方向に向けてください",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "🔄 ARCore実装準備中",
                            color = Color.Yellow,
                            fontSize = 12.sp
                        )
                    }
                }

                // ナビゲーション情報オーバーレイ
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎯 ${shelterName}",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "距離: ${String.format("%.0f", distance)}m",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "方角: ${getDirectionText(bearing)}",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }

                // 方向指示矢印
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(100.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF38A169).copy(alpha = 0.9f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "➤",
                                color = Color.White,
                                fontSize = 32.sp
                            )
                            Text(
                                text = getDirectionText(bearing),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 距離表示
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Blue.copy(alpha = 0.8f)
                    )
                ) {
                    Text(
                        text = "残り ${String.format("%.0f", distance)}m",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // 下部情報パネル
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📍 避難指示",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53E3E)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• ${getDirectionText(bearing)}方向に進んでください",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "• 慌てずに安全を確認しながら避難してください",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "• 緊急時は119番通報してください",
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 進行状況（修正版）
                    val progress = if (distance > 1000.0) {
                        0.1f
                    } else {
                        ((1000.0 - distance) / 1000.0).toFloat()
                    }

                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF38A169)
                    )

                    val progressPercentage = (progress * 100).toInt()
                    Text(
                        text = "避難進行度: ${progressPercentage}%",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }

    private fun calculateDistance(): Double {
        val earthRadius = 6371000.0 // メートル
        val dLat = Math.toRadians(shelterLat - userLat)
        val dLng = Math.toRadians(shelterLng - userLng)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(userLat)) * cos(Math.toRadians(shelterLat)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    private fun calculateBearing(): Double {
        val dLng = Math.toRadians(shelterLng - userLng)
        val lat1 = Math.toRadians(userLat)
        val lat2 = Math.toRadians(shelterLat)
        val y = sin(dLng) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLng)
        return Math.toDegrees(atan2(y, x))
    }

    private fun getDirectionText(bearing: Double): String {
        val normalizedBearing = (bearing + 360) % 360
        return when {
            normalizedBearing < 22.5 || normalizedBearing >= 337.5 -> "北"
            normalizedBearing < 67.5 -> "北東"
            normalizedBearing < 112.5 -> "東"
            normalizedBearing < 157.5 -> "南東"
            normalizedBearing < 202.5 -> "南"
            normalizedBearing < 247.5 -> "南西"
            normalizedBearing < 292.5 -> "西"
            normalizedBearing < 337.5 -> "北西"
            else -> "北"
        }
    }
}
