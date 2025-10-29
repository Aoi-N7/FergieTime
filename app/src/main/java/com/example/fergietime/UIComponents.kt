
/*
package com.example.fergietime
// Jetpack Compose UI関連
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

// Android関連
import android.Manifest
import android.content.Context
import android.location.Location
import android.widget.Toast

// Google Maps関連
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng

// Activity Result API（権限リクエスト）
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EvacuationNavApp(viewModel: ShelterViewModel = viewModel()) {
    val shelters by viewModel.shelters.collectAsState()

    val context = LocalContext.current

    // MapView を Compose 内で保持
    var mapView = remember { MapView(context, GoogleMapOptions()) }

    var googleMapRef: GoogleMap? by remember { mutableStateOf<GoogleMap?>(null) }
    var selectedShelter: EvacuationShelter? by remember { mutableStateOf<EvacuationShelter?>(null) }
    var currentLocationState: Location? by remember { mutableStateOf<Location?>(null) }
    var showShelterList by remember { mutableStateOf(false) }

    val sortedShelters by remember {
        derivedStateOf {
            currentLocationState?.let { location ->
                shelters.map { shelter ->
                    ShelterWithDistance(
                        shelter = shelter,
                        distance = calculateDistance(
                            location.latitude, location.longitude,
                            shelter.position.latitude, shelter.position.longitude
                        )
                    )
                }.sortedBy { it.distance }
            } ?: shelters.map { ShelterWithDistance(it, Double.MAX_VALUE) }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            googleMapRef?.let { map ->
                showLocationAndShelters(context, map) { location ->
                    currentLocationState = location
                }
            }
        } else {
            Toast.makeText(context, "位置情報の権限が必要です", Toast.LENGTH_LONG).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // ヘッダー部分
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xffd1e0f9)) // 背景色
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "避難ARナビ", // タイトル
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "最寄りの避難所を確認してください", // サブタイトル
                    fontSize = 18.sp,
                    color = Color.Black
                )
                // 現在地が取得できている場合に表示
                currentLocationState?.let {
                    Text(
                        text = "📍 現在地取得済み",
                        fontSize = 12.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // マップビューを配置するコンテナ
        Box(
            modifier = Modifier
                .fillMaxWidth() // 横幅いっぱいに広げる
                .weight(1f)     // 余ったスペースを占有（上のヘッダーとのバランス調整）
        ) {
            // Google Map を表示するためのAndroidネイティブビューをComposeに埋め込む
            AndroidView(
                factory = { ctx -> // MapView を生成する処理
                    MapView(ctx, GoogleMapOptions()).apply {
                        onCreate(null) // MapViewの初期化
                        mapView = this // 作成したMapViewを保持
                        getMapAsync { map -> // 非同期でGoogleMapのインスタンスを取得
                            googleMapRef = map // 参照を保存
                            setupMap(map, shelters) { shelter ->
                                selectedShelter = shelter
                            }


                            if (hasLocationPermission(context)) {
                                // 位置情報の権限がある場合 → 現在地と避難所を表示
                                showLocationAndShelters(context, map) { location ->
                                    currentLocationState = location
                                }
                            } else {
                                // 権限がない場合 → ユーザーにリクエスト
                                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize(), // MapViewを画面いっぱいに表示
                update = { mapView -> // 再描画時に呼ばれる処理
                    mapView.onStart()  // MapViewを再開
                    mapView.onResume() // MapViewを前面に表示状態に
                }
            )

            // 右下に配置するフローティングボタン群
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Boxの右下に配置
                    .padding(16.dp)             // 画面端から少し余白
            ) {
                // 避難所一覧を開くボタン
                FloatingActionButton(
                    onClick = { showShelterList = !showShelterList }, // 押すと一覧の表示/非表示を切り替える
                    containerColor = MaterialTheme.colorScheme.primary, // テーマカラー
                    modifier = Modifier.padding(bottom = 8.dp) // 下に余白
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "避難所一覧")
                }

                // 避難所が選択されている場合にだけ表示するボタン
                selectedShelter?.let { shelter ->
                    FloatingActionButton(
                        onClick = {
                            // ナビゲーション開始処理（ARナビへ遷移するイメージ）
                            showShelterInfo(context, shelter, currentLocationState)
                        },
                        containerColor = Color(0xFF38A169) // 緑色
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = "ARナビ開始")
                    }
                }
            }
        }

// ================= 避難所一覧表示部分 =================

// フラグが true のときに表示される（リストの開閉）
        if (showShelterList) {
            Card(
                modifier = Modifier
                    .fillMaxWidth() // 横幅いっぱい
                    .height(300.dp) // 高さ固定
                    .padding(8.dp)  // 外側の余白
            ) {
                Column {
                    // リストのヘッダー部分
                    Text(
                        text = if (currentLocationState != null) "📍 距離順（近い順）" else "避難所一覧",
                        fontWeight = FontWeight.Bold, // 太字
                        fontSize = 16.sp,             // 少し大きめ文字
                        modifier = Modifier.padding(12.dp),
                        color = if (currentLocationState != null) Color(0xFF38A169) else Color.Gray
                    )

                    // スクロール可能なリスト
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        // 距離順に並んだ避難所リストを表示
                        items(sortedShelters) { shelterWithDistance ->
                            ShelterListItem(
                                shelter = shelterWithDistance.shelter, // 避難所データ
                                distance = if (shelterWithDistance.distance != Double.MAX_VALUE)
                                    shelterWithDistance.distance else null, // 距離が分かるなら表示
                                isNearby = shelterWithDistance.distance < 1000, // 1km以内なら「近い」として扱う
                                onClick = {
                                    // リストのアイテムをタップしたとき
                                    selectedShelter = shelterWithDistance.shelter
                                    googleMapRef?.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            shelterWithDistance.shelter.position, 16f // 地図をその避難所にズーム
                                        )
                                    )
                                    showShelterList = false // リストを閉じる
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${getShelterTypeIcon(shelter.shelterType)} ${shelter.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (isNearby) {
                        Text(
                            text = " 🔥",
                            fontSize = 16.sp
                        )
                    }
                }
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
            // 距離表示
            distance?.let {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatDistance(it),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isNearby) Color(0xFF38A169) else Color.Blue
                    )
                    if (isNearby) {
                        Text(
                            text = "近い",
                            fontSize = 10.sp,
                            color = Color(0xFF38A169)
                        )
                    }
                }
            }
        }
    }
}

// ================== HELPER FUNCTIONS ==================

// 距離を人間にわかりやすい形式に変換する関数

*/