// Google Map 上に家族の位置や避難所を表示し、緊急メッセージや避難所情報をカードで提示する画面
package com.example.fergietime

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*

@Composable
fun MapScreen(activity: Activity, onPersonClick: (String) -> Unit, onNavigateToEvacuation: () -> Unit) {
    // 位置情報センサーを初期化
    val locationSensor = remember { LocationSensor(activity) }

    // 現在位置を監視
    val currentLocation by locationSensor.location.observeAsState()
    val currentLatLng = currentLocation?.let { LatLng(it.latitude, it.longitude) }

    // カメラ位置の状態を保持
    val cameraPositionState = rememberCameraPositionState()

    // 初回表示時に位置情報を取得
    LaunchedEffect(Unit) {
        locationSensor.fetchLocationAndStore()
    }

    // 現在位置が更新されたら地図のカメラを移動
    LaunchedEffect(currentLatLng) {
        currentLatLng?.let {
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(it, 16f))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Google Map 表示
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            // マーカー(仮)
            // あなたの位置（緑）
            Marker(
                state = remember { MarkerState(position = LatLng(34.6900, 135.1860)) },
                title = "あなた（10分前）",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )
            // 母の位置（オレンジ）
            Marker(
                state = remember { MarkerState(position = LatLng(34.6910, 135.1960)) },
                title = "母（29分前）",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            )
            // 祖父の位置（赤）
            Marker(
                state = remember { MarkerState(position = LatLng(34.6880, 135.1840)) },
                title = "祖父（59分前）",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )
            // 学校（避難所）（青）
            Marker(
                state = remember { MarkerState(position = LatLng(34.6920, 135.1900)) },
                title = "神戸市立中学校",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            )
        }

        // 下部にカード表示
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 緊急メッセージカード（祖父からのメッセージ）
            Card(elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("祖父（山田正男）", fontWeight = FontWeight.Bold)
                    Text("12時34分", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("救助が来て一人で避難できません。", color = Color.Red)
                    }
                    Text("さすがに笑えない状況", color = Color.Red)
                }
            }

            // 避難所カード（●●小学校）
            Card(elevation = CardDefaults.cardElevation(4.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color.Black, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("●●小学校", fontWeight = FontWeight.Bold)
                        Text("兵庫県神戸市中央区...", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("ここから 徒歩7分", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    // 避難所詳細画面への遷移ボタン
                    IconButton(onClick = onNavigateToEvacuation) {
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }
}

