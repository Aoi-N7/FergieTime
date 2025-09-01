package com.example.fergietime

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions

class LocationSensor(private val activity: Activity) {

    // 位置情報を監視するためのLiveData（内部で更新される）
    private val _location: MutableLiveData<Location> = MutableLiveData()

    // 外部から読み取り専用としてアクセス可能なLiveData
    val location: LiveData<Location> = _location

    // 一時的に保持する緯度と経度（Firestoreへ送信するために使用）
    var savedLatitude: Double? = null
    var savedLongitude: Double? = null

    // 位置情報の使用許可をユーザーにリクエストする
    fun requestPermission() {
        val LOCATION_PERMISSION_REQUEST_CODE = 1001

        // 許可が与えられているか確認
        val isGranted = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // 許可されていない場合はリクエストダイアログを表示
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    // 現在の位置情報を取得し、変数とLiveDataに保存する
    fun fetchLocationAndStore() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        // パーミッションが与えられていない場合は何もしない
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("LocationSensor", "位置情報の権限がありません")
            return
        }

        // 最後に取得された位置情報を取得する
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // 緯度・経度を変数に保存し、LiveDataにも設定
                    savedLatitude = location.latitude
                    savedLongitude = location.longitude
                    _location.postValue(location)
                    Log.d("LocationSensor", "取得成功: 緯度=$savedLatitude 経度=$savedLongitude")
                } else {
                    Log.d("LocationSensor", "位置情報はnullでした")
                }
            }
            .addOnFailureListener {
                Log.w("LocationSensor", "位置情報の取得に失敗しました", it)
            }
    }

    // 保存された位置情報をFirestoreに送信する
    fun uploadStoredLocationToFirestore(userId: String) {
        val lat = savedLatitude
        val lng = savedLongitude

        // 緯度・経度がnullでないことを確認
        if (lat != null && lng != null) {
            // Firestoreのuserコレクションのドキュメントを参照
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("user").document(userId)

            // GeoPoint形式で位置情報を作成
            val geoPoint = GeoPoint(lat, lng)
            val data = mapOf("location" to geoPoint)

            // データをFirestoreに送信（既存データに上書きしないようmergeオプションを使用）
            userRef.set(data, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("Firestore", "Firestoreに位置情報を送信しました: $lat, $lng")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "送信失敗", e)
                }
        } else {
            Log.w("Firestore", "保存された位置情報が null です")
        }
    }

    // 外部から位置情報の取得をリクエストするメソッド（fetchLocationAndStoreの呼び出し）
    fun requestCurrentLocation() {
        fetchLocationAndStore()
    }
}
