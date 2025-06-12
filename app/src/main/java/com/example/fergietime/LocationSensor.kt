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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationSensor(val activity: MainActivity) {

    // 位置情報が更新されたらこのLiveDataに格納する
    private val _location: MutableLiveData<Location> = MutableLiveData<Location>()
    val location: LiveData<Location> = _location

    fun requestPermission() {
        val LOCATION_PERMISSION_REQUEST_CODE = 1001

        val isAccept = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!isAccept) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun fusedLocation() {

        // 最後に確認された位置情報を取得
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity)

        // 一応権限のチェック
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("LocationSensor","権限がない")
            // 権限もらえないと困っちゃうなぁ
            return
        }

        // 位置情報を取得したらListenerが反応する
        fusedLocationClient.lastLocation
            .addOnSuccessListener(activity) { location ->
                Log.d("LocationSensor","$location")
                if (location != null) {
                    _location.postValue(location)
                }
            }
    }

    fun requestCurrentLocation() {
        val client = LocationServices.getFusedLocationProviderClient(activity)

        val request = com.google.android.gms.location.LocationRequest.Builder(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            1000L
        )
            //trueだと一定時間内に精度を満たせない → null が返る
            .setWaitForAccurateLocation(false)
            .setMaxUpdates(1)
            .build()

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("LocationSensor", "権限がありません")
            return
        }

        client.requestLocationUpdates(
            request,
            object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                    result.lastLocation?.let {
                        Log.d("LocationSensor", "現在位置取得: $it")
                        _location.postValue(it)
                    }
                    client.removeLocationUpdates(this)
                }
            },
            null
        )
    }


}