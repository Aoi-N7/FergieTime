package com.example.fergietime

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlin.math.*

@Composable
fun MapScreen(
    activity: Activity,
    onPersonClick: (String) -> Unit,
    onNavigateToEvacuation: () -> Unit,
    viewModel: ShelterViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // 現在地を取得するセンサー
    val locationSensor = remember { LocationSensor(activity) }

    // LiveDataで取得した現在地をComposeで監視
    val currentLocation by locationSensor.location.observeAsState()

    val context = LocalContext.current

    // ViewModelから避難所リストを取得（StateFlowをComposeで監視）
    val shelters by viewModel.shelters.collectAsState()

    // 選択された避難所
    var selectedShelter: EvacuationShelter? by remember { mutableStateOf(null) }

    // 避難所一覧を表示するかどうか
    var showShelterList by remember { mutableStateOf(false) }

    // 現在地をもとに避難所との距離を計算し、近い順に並び替え
    val sortedShelters by remember(currentLocation, shelters) {
        derivedStateOf {
            currentLocation?.let { loc ->
                shelters.map {
                    ShelterWithDistance(it, calculateDistance(loc.latitude, loc.longitude, it.position.latitude, it.position.longitude))
                }.sortedBy { it.distance } // 距離の昇順でソート
            } ?: shelters.map { ShelterWithDistance(it, Double.MAX_VALUE) }
        }
    }

    // GoogleMapのカメラ位置を管理
    val cameraPositionState = rememberCameraPositionState()

    // 位置情報の権限処理
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "位置情報の権限が必要です", Toast.LENGTH_LONG).show()
        }
    }

    // 画面表示時に一度だけ現在地を取得
    LaunchedEffect(permissionLauncher) {
        locationSensor.fetchLocationAndStore()
    }

    // 現在地が更新されたら地図のカメラをその位置へ移動
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 16f))
        }
    }

    // UI構成
    Box(modifier = Modifier.fillMaxSize()) {

        // 位置情報の権限リクエストを行い、権限が許可された場合のみ位置情報を表示
        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Google Mapの表示
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = currentLocation != null) // 現在地が取得できた場合のみ位置情報を表示
        ) {
            // 自分
            Marker(
                state = remember { MarkerState(position = LatLng(34.6900, 135.1860)) },
                title = "あなた（10分前）",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )
            // 母
            Marker(
                state = remember { MarkerState(position = LatLng(34.6910, 135.1960)) },
                title = "母（29分前）",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            )
            // 祖父
            Marker(
                state = remember { MarkerState(position = LatLng(34.6880, 135.1840)) },
                title = "祖父（59分前）",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )

            // 避難所マーカー
            shelters.forEach { shelter ->
                Marker(
                    state = MarkerState(position = shelter.position),
                    title = shelter.name,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                    onClick = {
                        selectedShelter = shelter
                        false
                    }
                )
            }
        }

//        // 下部 UI
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//                .align(Alignment.BottomCenter),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            // 緊急メッセージカード
//            Card(elevation = CardDefaults.cardElevation(4.dp)) {
//                Column(modifier = Modifier.padding(16.dp)) {
//                    Text("祖父（山田正男）", fontWeight = FontWeight.Bold)
//                    Text("12時34分", color = MaterialTheme.colorScheme.onSurfaceVariant)
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text("救助が来て一人で避難できません。", color = Color.Red)
//                    }
//                    Text("さすがに笑えない状況", color = Color.Red)
//                }
//            }
//
//            // 避難所情報カード（仮）
//            Card(elevation = CardDefaults.cardElevation(4.dp)) {
//                Row(
//                    modifier = Modifier.padding(16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(Icons.Default.School, contentDescription = null, tint = Color.Black, modifier = Modifier.size(48.dp))
//                    Spacer(modifier = Modifier.width(12.dp))
//                    Column(modifier = Modifier.weight(1f)) {
//                        Text("●●小学校", fontWeight = FontWeight.Bold)
//                        Text("兵庫県神戸市中央区...", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
//                        Text("ここから 徒歩7分", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
//                    }
//                    IconButton(onClick = onNavigateToEvacuation) {
//                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
//                    }
//                }
//            }
//        }

        // フローティングボタン
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = { showShelterList = !showShelterList },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = "避難所一覧")
            }

            selectedShelter?.let {
                Spacer(modifier = Modifier.height(8.dp))
                FloatingActionButton(
                    onClick = {
                        showShelterInfo(context, it, currentLocation)
                    },
                    containerColor = Color(0xFF38A169)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = "ナビ開始")
                }
            }
        }

        // 避難所一覧
        if (showShelterList) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .align(Alignment.TopCenter)
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = if (currentLocation != null) "📍 距離順（近い順）" else "避難所一覧",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(12.dp),
                        color = if (currentLocation != null) Color(0xFF38A169) else Color.Gray
                    )

                    LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
                        items(sortedShelters) { shelterWithDistance ->
                            ShelterListItem(
                                shelter = shelterWithDistance.shelter,
                                distance = if (shelterWithDistance.distance != Double.MAX_VALUE)
                                    shelterWithDistance.distance else null,
                                isNearby = shelterWithDistance.distance < 1000,
                                onClick = {
                                    selectedShelter = shelterWithDistance.shelter
                                    cameraPositionState.move(
                                        CameraUpdateFactory.newLatLngZoom(
                                            shelterWithDistance.shelter.position, 16f
                                        )
                                    )
                                    showShelterList = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShelterListItem(
    shelter: EvacuationShelter,
    distance: Double?,
    isNearby: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isNearby) Color(0xFFE6FFFA) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isNearby) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${getShelterTypeIcon(shelter.shelterType)} ${shelter.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = shelter.address,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "収容人数: ${shelter.capacity}人 | ${getSiteTypeName(shelter.siteType)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                if (shelter.facilities.isNotEmpty()) {
                    Text(
                        text = "設備: ${shelter.facilities.take(3).joinToString(", ")}",
                        fontSize = 11.sp,
                        color = Color.Blue
                    )
                }
            }

            distance?.let {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatDistance(it),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isNearby) Color(0xFF38A169) else Color.Blue
                    )
                    if (isNearby) {
                        Text("近い", fontSize = 10.sp, color = Color(0xFF38A169))
                    }
                }
            }
        }
    }
}


