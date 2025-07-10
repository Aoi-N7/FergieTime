package com.example.fergietime

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlin.math.*

class ArNavigationActivity : ComponentActivity(), SensorEventListener {

    private var shelterName: String = ""
    private var shelterLat: Double = 0.0
    private var shelterLng: Double = 0.0
    private var userLat: Double = 0.0
    private var userLng: Double = 0.0

    // センサー関連
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private var gyroscope: Sensor? = null

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    // AR状態
    private var currentAzimuth by mutableStateOf(0f)
    private var targetBearing by mutableStateOf(0.0)
    private var currentDistance by mutableStateOf(0.0)
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // インテントからデータを取得
        shelterName = intent.getStringExtra("shelter_name") ?: ""
        shelterLat = intent.getDoubleExtra("shelter_lat", 0.0)
        shelterLng = intent.getDoubleExtra("shelter_lng", 0.0)
        userLat = intent.getDoubleExtra("user_lat", 0.0)
        userLng = intent.getDoubleExtra("user_lng", 0.0)

        // センサーマネージャーの初期化
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // 初期計算
        targetBearing = calculateBearing(userLat, userLng, shelterLat, shelterLng)
        currentDistance = calculateDistance(userLat, userLng, shelterLat, shelterLng)

        setContent {
            MaterialTheme {
                ArNavigationScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // センサーリスナーを登録
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
            }
        }

        // 方位角を計算
        updateOrientationAngles()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // 精度変更時の処理（必要に応じて実装）
    }

    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        // 方位角を度に変換（0-360度）
        val azimuthInRadians = orientationAngles[0]
        val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
        currentAzimuth = if (azimuthInDegrees < 0) {
            azimuthInDegrees + 360f
        } else {
            azimuthInDegrees
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ArNavigationScreen() {
        val context = LocalContext.current
        var hasCameraPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            )
        }
        var cameraError by remember { mutableStateOf<String?>(null) }

        // 位置情報の更新
        LaunchedEffect(Unit) {
            while (true) {
                updateLocation()
                delay(2000) // 2秒ごとに位置情報を更新
            }
        }

        val cameraPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            hasCameraPermission = isGranted
            if (!isGranted) {
                Toast.makeText(context, "カメラの権限が必要です", Toast.LENGTH_LONG).show()
            }
        }

        LaunchedEffect(Unit) {
            if (!hasCameraPermission) {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ヘッダー
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🧭 ARナビゲーション",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = shelterName,
                            fontSize = 14.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE53E3E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )

            // ARカメラビュー
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (hasCameraPermission && cameraError == null) {
                    // カメラプレビュー
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        onError = { error ->
                            cameraError = error
                            Log.e("ArNavigationActivity", "Camera error: $error")
                        }
                    )

                    // ARオーバーレイ
                    ArOverlay(
                        modifier = Modifier.fillMaxSize(),
                        currentAzimuth = currentAzimuth,
                        targetBearing = targetBearing.toFloat(),
                        distance = currentDistance,
                        shelterName = shelterName
                    )
                } else {
                    // エラー表示
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (!hasCameraPermission) "📱 カメラ権限が必要です" else "📷 カメラエラー",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (cameraError != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = cameraError ?: "",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // 下部情報パネル
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "📍 距離",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE53E3E)
                            )
                            Text(
                                text = "${String.format("%.0f", currentDistance)}m",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Blue
                            )
                        }
                        Column {
                            Text(
                                text = "🧭 方位",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                            Text(
                                text = "${String.format("%.0f", currentAzimuth)}°",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Green
                            )
                        }
                        Column {
                            Text(
                                text = "🎯 目標方位",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFA500)
                            )
                            Text(
                                text = "${String.format("%.0f", targetBearing)}°",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Yellow
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 進行状況
                    val progress = if (currentDistance > 1000.0) {
                        0.1f
                    } else {
                        ((1000.0 - currentDistance) / 1000.0).toFloat().coerceIn(0f, 1f)
                    }

                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF38A169)
                    )

                    val progressPercentage = (progress * 100).toInt()
                    Text(
                        text = "避難進行度: ${progressPercentage}%",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }

    @Composable
    fun ArOverlay(
        modifier: Modifier = Modifier,
        currentAzimuth: Float,
        targetBearing: Float,
        distance: Double,
        shelterName: String
    ) {
        Canvas(modifier = modifier) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            // 目標方向への角度差を計算
            val angleDifference = normalizeAngle(targetBearing - currentAzimuth)

            // 矢印の位置を計算（画面中央から目標方向へ）
            val arrowDistance = 200f
            val arrowX = centerX + arrowDistance * sin(Math.toRadians(angleDifference.toDouble())).toFloat()
            val arrowY = centerY - arrowDistance * cos(Math.toRadians(angleDifference.toDouble())).toFloat()

            // 大きな方向矢印を描画
            drawDirectionArrow(
                center = Offset(arrowX, arrowY),
                rotation = angleDifference,
                color = if (abs(angleDifference) < 15f) Color.Green else Color.Red,
                size = 60f
            )

            // 距離表示
            drawDistanceIndicator(
                center = Offset(centerX, centerY - 100),
                distance = distance,
                shelterName = shelterName
            )

            // コンパス表示
            drawCompass(
                center = Offset(centerX, centerY + 150),
                currentAzimuth = currentAzimuth,
                targetBearing = targetBearing
            )
        }
    }

    private fun DrawScope.drawDirectionArrow(
        center: Offset,
        rotation: Float,
        color: Color,
        size: Float
    ) {
        rotate(rotation, center) {
            val path = Path().apply {
                moveTo(center.x, center.y - size)
                lineTo(center.x - size/2, center.y + size/2)
                lineTo(center.x, center.y + size/4)
                lineTo(center.x + size/2, center.y + size/2)
                close()
            }
            drawPath(path, color)
        }
    }

    private fun DrawScope.drawDistanceIndicator(
        center: Offset,
        distance: Double,
        shelterName: String
    ) {
        // 距離表示の背景
        drawCircle(
            color = Color.Black.copy(alpha = 0.7f),
            radius = 80f,
            center = center
        )

        // 距離テキストは別途Textでオーバーレイするのでここでは円のみ描画
    }

    private fun DrawScope.drawCompass(
        center: Offset,
        currentAzimuth: Float,
        targetBearing: Float
    ) {
        // コンパスの外枠
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = 50f,
            center = center
        )
        drawCircle(
            color = Color.Black,
            radius = 48f,
            center = center
        )

        // 現在の方位を示す線
        val currentX = center.x + 40 * sin(Math.toRadians(currentAzimuth.toDouble())).toFloat()
        val currentY = center.y - 40 * cos(Math.toRadians(currentAzimuth.toDouble())).toFloat()
        drawLine(
            color = Color.Blue,
            start = center,
            end = Offset(currentX, currentY),
            strokeWidth = 4f
        )

        // 目標方位を示す線
        val targetX = center.x + 35 * sin(Math.toRadians(targetBearing.toDouble())).toFloat()
        val targetY = center.y - 35 * cos(Math.toRadians(targetBearing.toDouble())).toFloat()
        drawLine(
            color = Color.Red,
            start = center,
            end = Offset(targetX, targetY),
            strokeWidth = 3f
        )
    }

    @Composable
    fun CameraPreview(
        modifier: Modifier = Modifier,
        onError: (String) -> Unit
    ) {
        val lifecycleOwner = LocalLifecycleOwner.current
        val context = LocalContext.current

        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder()
                            .build()
                            .also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview
                        )

                        Log.d("ArNavigationActivity", "Camera preview started successfully")

                    } catch (exc: Exception) {
                        Log.e("ArNavigationActivity", "Camera preview failed", exc)
                        onError("カメラの初期化に失敗しました: ${exc.message}")
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = modifier
        )
    }

    private fun updateLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation = it
                    userLat = it.latitude
                    userLng = it.longitude

                    // 目標方位と距離を再計算
                    targetBearing = calculateBearing(userLat, userLng, shelterLat, shelterLng)
                    currentDistance = calculateDistance(userLat, userLng, shelterLat, shelterLng)
                }
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0 // メートル
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    private fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLng = Math.toRadians(lon2 - lon1)
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val y = sin(dLng) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(dLng)
        val bearing = Math.toDegrees(atan2(y, x))
        return if (bearing < 0) bearing + 360 else bearing
    }

    private fun normalizeAngle(angle: Float): Float {
        var normalized = angle % 360
        if (normalized > 180) normalized -= 360
        if (normalized < -180) normalized += 360
        return normalized
    }
}
