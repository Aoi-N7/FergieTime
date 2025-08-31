package com.example.fergietime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fergietime.ui.theme.FergieTimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FergieTimeTheme {

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Columnで縦に並べる
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Greeting(name = "FergieTimea")

//                      mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
//                            Log.d("Mapbox", "Style loaded successfully")
//                         }

                        MapScreen() // Mapを画面の残りに表示
                    }
                }
            }

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FergieTimeTheme {
        Greeting("Android")
    }
}