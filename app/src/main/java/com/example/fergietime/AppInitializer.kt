package com.example.fergietime

import android.os.Build
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.example.fergietime.Notice
import com.example.fergietime.LocationSensor
import androidx.activity.enableEdgeToEdge

fun initializeAppLogic(activity: MainActivity) {
    FirebaseApp.initializeApp(activity)
    activity.enableEdgeToEdge()
    Notice.createChannel(activity)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activity.requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }
    }

    activity.locationSensor = LocationSensor(activity)
    activity.locationSensor.requestPermission()
    activity.locationSensor.requestCurrentLocation()
}
