package com.example.fergietime

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke

@Composable
fun LocationScreen(
    locationSensor: LocationSensor,
    onNavigateTo: (Screen) -> Unit
) {
    var latitude by remember { mutableStateOf("未取得") }
    var longitude by remember { mutableStateOf("未取得") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 緯度・経度 表示
        Text("緯度: $latitude", fontSize = 16.sp)
        Text("経度: $longitude", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // 各種ボタン
        SettingCardButton(Icons.Default.LocationOn, "位置情報を取得") {
            locationSensor.requestCurrentLocation()
            val location = locationSensor.location.value
            if (location != null) {
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
            }
        }

        SettingCardButton(Icons.Default.Send, "位置情報を送信") {
            val location = locationSensor.location.value
            if (location != null) {
                uploadLocationToFirestore(location.latitude, location.longitude)
            }
        }

        SettingCardButton(Icons.Default.Notifications, "通知設定へ") {
            onNavigateTo(Screen.Notification)
        }

        SettingCardButton(Icons.Default.Delete, "キャッシュ削除画面へ") {
            onNavigateTo(Screen.Cache)
        }

        SettingCardButton(Icons.Default.Person, "ログイン画面へ") {
            onNavigateTo(Screen.Login)
        }
    }
}

@Composable
fun SettingCardButton(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface, // 白背景（テーマ準拠）
        shadowElevation = 6.dp, // 陰影追加（立体感）
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)), // 淵線（薄めのグレー）
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
