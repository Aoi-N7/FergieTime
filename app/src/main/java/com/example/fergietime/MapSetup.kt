package com.example.fergietime

// Android関連
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast

// 権限確認
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity

// Google Play Services関連
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

// Google Maps関連
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory

// 位置情報取得
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority



fun setupMap(
    googleMap: GoogleMap,
    shelters: List<EvacuationShelter>,
    onMarkerClick: (EvacuationShelter) -> Unit
) {
    // 地図のUI設定
    googleMap.uiSettings.apply {
        isZoomControlsEnabled = true     // ズームボタンを表示
        isCompassEnabled = true          // コンパスを表示
        isMyLocationButtonEnabled = true // 「現在地に移動」ボタンを表示
    }

    // 避難所マーカーを地図に追加
    shelters.forEach { shelter ->
        val marker = googleMap.addMarker(
            MarkerOptions()
                .position(shelter.position) // 緯度経度
                .title(shelter.name) // マーカーのタイトル（避難所名）
                .snippet("収容人数: ${shelter.capacity}人 | ${getSiteTypeName(shelter.siteType)}") // サブ情報
                .icon(
                    BitmapDescriptorFactory.defaultMarker(
                        when (shelter.siteType) {
                            // 避難所の種類に応じてマーカーの色を変える
                            EvacuationSiteType.WIDE_AREA_EVACUATION_SITE -> BitmapDescriptorFactory.HUE_GREEN
                            EvacuationSiteType.DESIGNATED_EMERGENCY_EVACUATION_SITE -> BitmapDescriptorFactory.HUE_ORANGE
                            EvacuationSiteType.TSUNAMI_EVACUATION_BUILDING -> BitmapDescriptorFactory.HUE_BLUE
                            else -> BitmapDescriptorFactory.HUE_RED
                        }
                    )
                )
        )
        marker?.tag = shelter // マーカーに避難所データを紐づけ
    }

    // マーカーがタップされたときの処理
    googleMap.setOnMarkerClickListener { marker ->
        val shelter = marker.tag as? EvacuationShelter // タップしたマーカーから避難所データを取り出す
        shelter?.let { onMarkerClick(it) } // コールバックで呼び出し元に通知
        false // falseを返すと標準のマーカークリック挙動（情報ウィンドウ表示）が継続
    }
}


// 現在地を取得し、GoogleMapに表示＆避難所も表示する処理
fun showLocationAndShelters(
    context: Context,
    googleMap: GoogleMap,
    onLocationUpdate: (Location) -> Unit // コールバック：現在地が取れたら呼ばれる
) {
    Log.d("MainActivity", "showLocationAndShelters called")

    // Google Play Services が利用可能か確認
    val googleApiAvailability = GoogleApiAvailability.getInstance()
    val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
    if (resultCode != ConnectionResult.SUCCESS) {
        // 利用できなければエラーメッセージを表示して終了
        Log.e("MainActivity", "Google Play Services not available: $resultCode")
        Toast.makeText(context, "Google Play Servicesが利用できません", Toast.LENGTH_LONG).show()
        return
    }

    // 位置情報を扱うクライアントを取得
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // 権限がなければ終了
    if (!hasLocationPermission(context)) {
        Log.w("MainActivity", "Location permission not granted")
        return
    }

    try {
        // マップに「現在地」ボタンを有効化
        googleMap.isMyLocationEnabled = true
        Log.d("MainActivity", "Requesting location...")

        // 最後に記録された位置情報を取得
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // 位置情報が取得できた場合
                Log.d("MainActivity", "Location found: ${location.latitude}, ${location.longitude}")

                onLocationUpdate(location) // コールバック実行
                val userLatLng = LatLng(location.latitude, location.longitude)
                // カメラを現在地に移動
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f))
                Toast.makeText(context, "現在地を取得しました", Toast.LENGTH_SHORT).show()
            } else {
                // null の場合は改めてリクエストを行う
                Log.w("MainActivity", "Last known location is null, requesting current location")
                requestCurrentLocation(context, googleMap, fusedLocationClient, onLocationUpdate)
            }
        }.addOnFailureListener { exception ->
            // 失敗時の処理
            Log.e("MainActivity", "Failed to get location", exception)
            Toast.makeText(context, "位置情報の取得に失敗しました: ${exception.message}", Toast.LENGTH_LONG).show()
            requestCurrentLocation(context, googleMap, fusedLocationClient, onLocationUpdate)
        }
    } catch (e: SecurityException) {
        // 権限がない場合の例外処理
        Log.e("MainActivity", "Security exception when accessing location", e)
        Toast.makeText(context, "位置情報の権限が必要です", Toast.LENGTH_SHORT).show()
    }
}

// 現在の位置を「リアルタイム」でリクエストする処理
private fun requestCurrentLocation(
    context: Context,
    googleMap: GoogleMap,
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocationUpdate: (Location) -> Unit
) {
    try {
        // 高精度の位置情報をリクエスト（10秒間隔・最小5秒・最大1回だけ）
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            10000L // 10秒間隔
        ).apply {
            setMinUpdateIntervalMillis(5000L) // 最小 5秒
            setMaxUpdates(1) // 1回だけ取得
        }.build()

        // 位置情報更新のコールバック
        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.d("MainActivity", "Current location found: ${location.latitude}, ${location.longitude}")

                    onLocationUpdate(location) // コールバック実行
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    // カメラを現在地に移動
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f))
                    Toast.makeText(context, "現在地を更新しました", Toast.LENGTH_SHORT).show()
                    fusedLocationClient.removeLocationUpdates(this) // 1回だけなのでリスナー解除
                }
            }
        }

        // 現在地リクエストを開始
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        Log.d("MainActivity", "Requesting current location updates")

    } catch (e: SecurityException) {
        // 権限がない場合の例外処理
        Log.e("MainActivity", "Security exception when requesting location updates", e)
        Toast.makeText(context, "位置情報の取得権限がありません", Toast.LENGTH_LONG).show()
    }
}

// 避難所情報を表示、またはARナビを開始する処理
fun showShelterInfo(
    context: Context,
    shelter: EvacuationShelter,
    currentLoc: Location?
) {
    currentLoc?.let { location ->
        val intent = Intent(context, ArNavigationActivity::class.java).apply {
            putExtra("shelter_name", shelter.name)
            putExtra("shelter_lat", shelter.position.latitude)
            putExtra("shelter_lng", shelter.position.longitude)
            putExtra("user_lat", location.latitude)
            putExtra("user_lng", location.longitude)
            putExtra("shelter_capacity", shelter.capacity)
            putExtra("shelter_facilities", shelter.facilities.joinToString(", "))
            putExtra("shelter_phone", shelter.phoneNumber ?: "")
            putExtra("shelter_address", shelter.address)
        }
        context.startActivity(intent)
    } ?: run {
        Toast.makeText(
            context,
            "${shelter.name}\n${shelter.address}\n収容人数: ${shelter.capacity}人\n設備: ${shelter.facilities.joinToString(", ")}",
            Toast.LENGTH_LONG
        ).show()
    }
}
// 2地点の緯度経度から距離（メートル）を計算（ハーサイン公式）
