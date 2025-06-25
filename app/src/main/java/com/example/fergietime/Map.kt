package com.example.fergietime

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // MapView を Compose で表示
    val mapView = remember {
        MapView(context).apply {
            getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}
