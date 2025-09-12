package com.example.fergietime

// Android関連
import android.content.Context
import android.location.Location
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log

// 権限確認
import androidx.core.app.ActivityCompat

// 数学関数（距離計算用）
import kotlin.math.*


fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371000.0 // 地球の半径（メートル）
    val dLat = Math.toRadians(lat2 - lat1) // 緯度差（ラジアン）
    val dLon = Math.toRadians(lon2 - lon1) // 経度差（ラジアン）
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2) // ハーサイン公式のa
    val c = 2 * atan2(sqrt(a), sqrt(1 - a)) // 中心角
    return earthRadius * c // 距離（メートル）
}

// 位置情報の権限が付与されているか確認
fun hasLocationPermission(context: Context): Boolean {
    val fineLocationGranted = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarseLocationGranted = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    Log.d("MainActivity", "Fine location: $fineLocationGranted, Coarse location: $coarseLocationGranted")
    return fineLocationGranted || coarseLocationGranted // どちらかでも許可されていればtrue
}
