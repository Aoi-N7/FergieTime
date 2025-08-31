// マップの表示（後々、現在値の取得、周辺の避難所の取得など追加予定）
package com.example.fergietime

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*

@Composable
fun MapScreen() {
    // 出発地点（元町駅）の緯度経度
    val origin = LatLng(34.6896, 135.1873)

    // 到着地点（三ノ宮駅）の緯度経度
    val destination = LatLng(34.6950, 135.1956)

    // カメラの初期位置を設定（出発地点を中心にズームレベル14.5）
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(origin, 14.5f)
    }

    // ルート取得などのユーティリティクラスをインスタンス化
    val repository = remember { MapUtils() }

    // ルートの座標リスト（Polyline描画用）
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    // 所要時間のテキスト（徒歩時間など）
    var durationText by remember { mutableStateOf("") }

    // ルート表示のトリガー（ボタンでtrueになる）
    var showRoute by remember { mutableStateOf(false) }

    // 出発地点のマーカー状態
    val originMarkerState = remember { MarkerState(position = origin) }

    // 到着地点のマーカー状態
    val destinationMarkerState = remember { MarkerState(position = destination) }

    // showRouteがtrueになったときにルート情報を取得
    LaunchedEffect(showRoute) {
        if (showRoute) {
            // Google Directions APIを使ってルートのPolyline座標を取得
            routePoints = repository.fetchRoutePolyline(origin, destination, BuildConfig.MAPS_API_KEY)

            // 所要時間（徒歩）を取得
            durationText = repository.fetchDuration(origin, destination, BuildConfig.MAPS_API_KEY)

            // 到着地点のマーカーに情報ウィンドウを表示
            destinationMarkerState.showInfoWindow()
        }
    }

    // 画面全体に地図とUIを配置
    Box(modifier = Modifier.fillMaxSize()) {
        // Google Mapの表示
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // 出発地点のマーカー（元町駅）
            Marker(state = originMarkerState, title = "元町駅")

            // 到着地点のマーカー（三ノ宮駅）
            // 所要時間が取得できていればスニペットとして表示
            Marker(
                state = destinationMarkerState,
                title = "三ノ宮駅",
                snippet = if (durationText.isNotEmpty()) "所要時間（徒歩）: $durationText" else null
            )

            // ルートが取得できていればPolylineで地図上に描画
            if (routePoints.isNotEmpty()) {
                Polyline(points = routePoints, color = Color.Blue, width = 8f)
            }
        }

        // 画面下部にルート案内開始ボタンを配置(他メンバーとの結合後調整)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            Button(onClick = { showRoute = true }) {
                Text("ルート案内を開始")
            }
        }
    }
}
