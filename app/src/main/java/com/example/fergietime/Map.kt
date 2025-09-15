//// マップの表示（後々、現在値の取得、周辺の避難所の取得など追加予定）
//package com.example.fergietime
//
//import android.app.Activity
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.model.*
//import com.google.maps.android.compose.*
//
//@Composable
//fun MapScreen2(activity: Activity) {
//
//    // 現在地取得用
//    val locationSensor = remember { LocationSensor(activity) }
//    val currentLocation by locationSensor.location.observeAsState()
//    val currentLatLng = currentLocation?.let { LatLng(it.latitude, it.longitude) }
//    val cameraPositionState = rememberCameraPositionState()
//
//    LaunchedEffect(Unit) {
//        locationSensor.fetchLocationAndStore()
//    }
//
//    val origin = LatLng(34.6896, 135.1873)
//    val destination = LatLng(34.6950, 135.1956)
//
//    val repository = remember { MapUtils() }
//    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
//    var durationText by remember { mutableStateOf("") }
//    var showRoute by remember { mutableStateOf(false) }
//
//    val originMarkerState = remember { MarkerState(position = origin) }
//    val destinationMarkerState = remember { MarkerState(position = destination) }
//
//
//    LaunchedEffect(currentLatLng) {
//        currentLatLng?.let {
//            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 16f))
//        }
//    }
//
//    LaunchedEffect(showRoute) {
//        if (showRoute) {
//            routePoints = repository.fetchRoutePolyline(origin, destination, BuildConfig.MAPS_API_KEY)
//            durationText = repository.fetchDuration(origin, destination, BuildConfig.MAPS_API_KEY)
//            destinationMarkerState.showInfoWindow()
//        }
//    }
//
//    // 画面全体に地図とUIを配置
//    Box(modifier = Modifier.fillMaxSize()) {
//        // Google Mapの表示
//        GoogleMap(
//            modifier = Modifier.fillMaxSize(),
//            cameraPositionState = cameraPositionState,
//            properties = MapProperties(isMyLocationEnabled = true)
//        ) {
//            Marker(state = originMarkerState, title = "元町駅")
//            Marker(
//                state = destinationMarkerState,
//                title = "三ノ宮駅",
//                snippet = if (durationText.isNotEmpty()) "所要時間（徒歩）: $durationText" else null
//            )
//
//            if (routePoints.isNotEmpty()) {
//                Polyline(points = routePoints, color = Color.Blue, width = 8f)
//            }
//        }
//
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//                .align(Alignment.BottomCenter)
//        ) {
//            Button(onClick = { showRoute = true }) {
//                Text("ルート案内を開始")
//            }
//        }
//    }
//}
