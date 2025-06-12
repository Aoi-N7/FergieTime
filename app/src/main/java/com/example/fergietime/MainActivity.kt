package com.example.fergietime

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp  // ← dp のために追加
import com.example.fergietime.ui.theme.FergieTimeTheme
import com.example.fergietime.LocationSensor  // ← LocationSensor のために追加



class MainActivity : ComponentActivity() {
    private lateinit var locationSensor: LocationSensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // LocationSensor 初期化 & 権限リクエスト
        locationSensor = LocationSensor(this)
        locationSensor.requestPermission()
        locationSensor.requestCurrentLocation()


        setContent {
            FergieTimeTheme {
                var latitude by remember { mutableStateOf("未取得") }
                var longitude by remember { mutableStateOf("未取得") }

                // LiveData を observe する
                DisposableEffect(Unit) {
                    val observer = androidx.lifecycle.Observer { location: android.location.Location ->
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                    }
                    locationSensor.location.observe(this@MainActivity, observer)

                    onDispose {
                        locationSensor.location.removeObserver(observer)
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "FergieTimea",
                        latitude = latitude,
                        longitude = longitude,
                        onRequestLocation = { locationSensor.fusedLocation() },
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    latitude: String,
    longitude: String,
    onRequestLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        Text(text = "Hello $name!")
        Text("緯度: $latitude")
        Text("経度: $longitude")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestLocation) {
            Text("位置情報を取得")
        }
    }
}