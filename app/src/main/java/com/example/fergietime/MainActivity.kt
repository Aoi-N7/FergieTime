package com.example.fergietime

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import androidx.compose.material3.MaterialTheme
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import kotlin.math.*
import androidx.compose.foundation.clickable
import android.content.Intent
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

// シンプルな避難所データクラス
data class SimpleShelter(
    val id: String,
    val name: String,
    val address: String,
    val position: LatLng,
    val capacity: Int,
    val info: String
)

// 距離付き避難所データクラス
data class ShelterWithDistance(
    val shelter: SimpleShelter,
    val distance: Double
)

class MainActivity : ComponentActivity() {

    private var mapView: MapView? = null
    private var currentLocation: Location? = null

    // サンプル避難所データ（東京都渋谷区周辺 + 兵庫県神戸市三ノ宮駅周辺）
    private val shelters = listOf(
        // 東京都渋谷区周辺
        SimpleShelter(
            id = "1",
            name = "渋谷区立中央小学校",
            address = "東京都渋谷区○○1-1-1",
            position = LatLng(35.6762, 139.6503),
            capacity = 500,
            info = "体育館、校庭、給水設備、非常用電源"
        ),
        SimpleShelter(
            id = "2",
            name = "代々木公園",
            address = "東京都渋谷区△△2-2-2",
            position = LatLng(35.6794, 139.6569),
            capacity = 300,
            info = "一時避難場所、広場、トイレ、水道"
        ),
        SimpleShelter(
            id = "3",
            name = "渋谷区民会館",
            address = "東京都渋谷区□□3-3-3",
            position = LatLng(35.6731, 139.6448),
            capacity = 200,
            info = "会議室、調理室、医務室、非常用電源"
        ),
        SimpleShelter(
            id = "4",
            name = "恵比寿ガーデンプレイス",
            address = "東京都渋谷区恵比寿4-20-3",
            position = LatLng(35.6640, 139.7130),
            capacity = 400,
            info = "一時避難場所、広場"
        ),

        // 兵庫県神戸市三ノ宮駅周辺
        SimpleShelter(
            id = "5",
            name = "神戸市立中央小学校",
            address = "兵庫県神戸市中央区中山手通4-23-2",
            position = LatLng(34.6937, 135.1955),
            capacity = 600,
            info = "体育館、校庭、給水設備、非常用電源、医務室"
        ),
        SimpleShelter(
            id = "6",
            name = "東遊園地",
            address = "兵庫県神戸市中央区加納町6-4-1",
            position = LatLng(34.6851, 135.1947),
            capacity = 1000,
            info = "広域避難場所、広場、トイレ、水道、防災倉庫"
        ),
        SimpleShelter(
            id = "7",
            name = "神戸市役所",
            address = "兵庫県神戸市中央区加納町6-5-1",
            position = LatLng(34.6851, 135.1956),
            capacity = 800,
            info = "災害対策本部、会議室、非常用電源、通信設備"
        ),
        SimpleShelter(
            id = "8",
            name = "兵庫県公館",
            address = "兵庫県神戸市中央区下山手通4-4-1",
            position = LatLng(34.6918, 135.1889),
            capacity = 300,
            info = "会議室、ホール、非常用電源"
        ),
        SimpleShelter(
            id = "9",
            name = "神戸国際会館",
            address = "兵庫県神戸市中央区御幸通8-1-6",
            position = LatLng(34.6919, 135.1975),
            capacity = 1200,
            info = "大ホール、会議室、レストラン、非常用電源"
        ),
        SimpleShelter(
            id = "10",
            name = "生田神社",
            address = "兵庫県神戸市中央区下山手通1-2-1",
            position = LatLng(34.6919, 135.1947),
            capacity = 400,
            info = "境内、社務所、トイレ、水道"
        ),
        SimpleShelter(
            id = "11",
            name = "神戸市立葺合高等学校",
            address = "兵庫県神戸市中央区野崎通1-1-1",
            position = LatLng(34.6889, 135.2019),
            capacity = 700,
            info = "体育館、校庭、教室、給水設備、非常用電源"
        ),
        SimpleShelter(
            id = "12",
            name = "HAT神戸・なぎさ公園",
            address = "兵庫県神戸市中央区脇浜海岸通1-3",
            position = LatLng(34.7056, 135.2167),
            capacity = 1500,
            info = "広域避難場所、広場、防災施設、ヘリポート"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                EvacuationNavApp()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EvacuationNavApp() {
        val context = LocalContext.current
        var googleMapRef by remember { mutableStateOf<GoogleMap?>(null) }
        var selectedShelter by remember { mutableStateOf<SimpleShelter?>(null) }
        var showShelterList by remember { mutableStateOf(false) }
        var currentLocationState by remember { mutableStateOf<Location?>(null) }

        // 現在地に基づいて距離順にソートされた避難所リスト
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
            // ヘッダー
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE53E3E))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🚨 避難ナビ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "最寄りの避難所を確認してください",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    // 現在地の状態を表示
                    currentLocationState?.let {
                        Text(
                            text = "📍 現在地取得済み",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // マップビュー
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AndroidView(
                    factory = { ctx ->
                        MapView(ctx, GoogleMapOptions()).apply {
                            onCreate(null)
                            mapView = this
                            getMapAsync { map ->
                                googleMapRef = map
                                setupMap(map) { shelter ->
                                    selectedShelter = shelter
                                }

                                if (hasLocationPermission(context)) {
                                    showLocationAndShelters(context, map) { location ->
                                        currentLocationState = location
                                    }
                                } else {
                                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { mapView ->
                        mapView.onStart()
                        mapView.onResume()
                    }
                )

                // フローティングボタン
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = { showShelterList = !showShelterList },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "避難所一覧")
                    }

                    selectedShelter?.let { shelter ->
                        FloatingActionButton(
                            onClick = {
                                showShelterInfo(shelter, currentLocationState)
                            },
                            containerColor = Color(0xFF38A169)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = "避難所情報")
                        }
                    }
                }
            }

            // 避難所一覧（距離順にソート済み）
            if (showShelterList) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(8.dp)
                ) {
                    Column {
                        // ヘッダー
                        Text(
                            text = if (currentLocationState != null) "📍 距離順（近い順）" else "避難所一覧",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(12.dp),
                            color = if (currentLocationState != null) Color(0xFF38A169) else Color.Gray
                        )

                        LazyColumn(
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            items(sortedShelters) { shelterWithDistance ->
                                ShelterListItem(
                                    shelter = shelterWithDistance.shelter,
                                    distance = if (shelterWithDistance.distance != Double.MAX_VALUE)
                                        shelterWithDistance.distance else null,
                                    isNearby = shelterWithDistance.distance < 1000, // 1km以内は近い
                                    onClick = {
                                        selectedShelter = shelterWithDistance.shelter
                                        googleMapRef?.animateCamera(
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
        shelter: SimpleShelter,
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
                            text = shelter.name,
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
                        text = "収容人数: ${shelter.capacity}人",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
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

    private fun formatDistance(distance: Double): String {
        return when {
            distance < 1000 -> "${distance.toInt()}m"
            distance < 10000 -> "${"%.1f".format(distance / 1000)}km"
            else -> "${(distance / 1000).toInt()}km"
        }
    }

    private fun setupMap(googleMap: GoogleMap, onMarkerClick: (SimpleShelter) -> Unit) {
        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = true
        }

        // 避難所マーカーを追加
        shelters.forEach { shelter ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(shelter.position)
                    .title(shelter.name)
                    .snippet("収容人数: ${shelter.capacity}人")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            marker?.tag = shelter
        }

        googleMap.setOnMarkerClickListener { marker ->
            val shelter = marker.tag as? SimpleShelter
            shelter?.let { onMarkerClick(it) }
            false
        }
    }

    private fun showLocationAndShelters(
        context: Context,
        googleMap: GoogleMap,
        onLocationUpdate: (Location) -> Unit
    ) {
        Log.d("MainActivity", "showLocationAndShelters called")

        // Google Play Servicesの可用性をチェック
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e("MainActivity", "Google Play Services not available: $resultCode")
            Toast.makeText(context, "Google Play Servicesが利用できません", Toast.LENGTH_LONG).show()
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (!hasLocationPermission(context)) {
            Log.w("MainActivity", "Location permission not granted")
            return
        }

        try {
            googleMap.isMyLocationEnabled = true
            Log.d("MainActivity", "Requesting location...")

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("MainActivity", "Location found: ${location.latitude}, ${location.longitude}")
                    currentLocation = location
                    onLocationUpdate(location)
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f))
                    Toast.makeText(context, "現在地を取得しました", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w("MainActivity", "Last known location is null, requesting current location")
                    requestCurrentLocation(context, googleMap, fusedLocationClient, onLocationUpdate)
                }
            }.addOnFailureListener { exception ->
                Log.e("MainActivity", "Failed to get location", exception)
                Toast.makeText(context, "位置情報の取得に失敗しました: ${exception.message}", Toast.LENGTH_LONG).show()
                requestCurrentLocation(context, googleMap, fusedLocationClient, onLocationUpdate)
            }
        } catch (e: SecurityException) {
            Log.e("MainActivity", "Security exception when accessing location", e)
            Toast.makeText(context, "位置情報の権限が必要です", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestCurrentLocation(
        context: Context,
        googleMap: GoogleMap,
        fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
        onLocationUpdate: (Location) -> Unit
    ) {
        try {
            val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                10000L // 10秒間隔
            ).apply {
                setMinUpdateIntervalMillis(5000L) // 最小5秒間隔
                setMaxUpdates(1) // 1回だけ取得
            }.build()

            val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        Log.d("MainActivity", "Current location found: ${location.latitude}, ${location.longitude}")
                        currentLocation = location
                        onLocationUpdate(location)
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f))
                        Toast.makeText(context, "現在地を更新しました", Toast.LENGTH_SHORT).show()
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            Log.d("MainActivity", "Requesting current location updates")

        } catch (e: SecurityException) {
            Log.e("MainActivity", "Security exception when requesting location updates", e)
            Toast.makeText(context, "位置情報の取得権限がありません", Toast.LENGTH_LONG).show()
        }
    }

    private fun showShelterInfo(shelter: SimpleShelter, currentLoc: Location?) {
        currentLoc?.let { location ->
            val distance = calculateDistance(
                location.latitude, location.longitude,
                shelter.position.latitude, shelter.position.longitude
            )

            // ARナビゲーションを開始
            val intent = Intent(this, ArNavigationActivity::class.java).apply {
                putExtra("shelter_name", shelter.name)
                putExtra("shelter_lat", shelter.position.latitude)
                putExtra("shelter_lng", shelter.position.longitude)
                putExtra("user_lat", location.latitude)
                putExtra("user_lng", location.longitude)
            }
            startActivity(intent)
        } ?: run {
            Toast.makeText(
                this,
                "${shelter.name}\n設備: ${shelter.info}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0 // メートル
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    private fun hasLocationPermission(context: Context): Boolean {
        val fineLocationGranted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        Log.d("MainActivity", "Fine location: $fineLocationGranted, Coarse location: $coarseLocationGranted")
        return fineLocationGranted || coarseLocationGranted
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapView?.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}
