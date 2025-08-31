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
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlin.math.*

class ArNavigationActivity : ComponentActivity(), SensorEventListener {

    // インテントから受け取るデータ
    private var shelterName: String = ""
    private var shelterLat: Double = 0.0
    private var shelterLng: Double = 0.0
    private var userLat: Double = 0.0
    private var userLng: Double = 0.0
    private var shelterCapacity: Int = 0
    private var shelterFacilities: String = ""
    private var shelterPhone: String = ""
    private var shelterAddress: String = ""

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
    private var currentAzimuth by mutableStateOf(0.0)
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
        shelterCapacity = intent.getIntExtra("shelter_capacity", 0)
        shelterFacilities = intent.getStringExtra("shelter_facilities") ?: ""
        shelterPhone = intent.getStringExtra("shelter_phone") ?: ""
        shelterAddress = intent.getStringExtra("shelter_address") ?: ""

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
        val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble())
        currentAzimuth = if (azimuthInDegrees < 0) {
            azimuthInDegrees + 360.0
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
        var showShelterDetails by remember { mutableStateOf(false) }

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

        Box(modifier = Modifier.fillMaxSize()) {
            // ARカメラビュー
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

                // 🎯 プレミアムUI オーバーレイ
                PremiumArOverlay(
                    modifier = Modifier.fillMaxSize(),
                    distance = currentDistance,
                    shelterName = shelterName,
                    targetBearing = targetBearing.toFloat(),
                    currentAzimuth = currentAzimuth
                )

            } else {
                // エラー表示
                ErrorScreen(
                    hasCameraPermission = hasCameraPermission,
                    cameraError = cameraError
                )
            }

            // トップバー
            PremiumTopBar(
                shelterName = shelterName,
                onBackClick = { finish() },
                onInfoClick = { showShelterDetails = !showShelterDetails }
            )

            // ボトムパネル
            PremiumBottomPanel(
                distance = currentDistance,
                currentAzimuth = currentAzimuth,
                targetBearing = targetBearing,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            // 避難所詳細パネル
            if (showShelterDetails) {
                ShelterDetailsPanel(
                    modifier = Modifier.align(Alignment.Center),
                    onDismiss = { showShelterDetails = false }
                )
            }
        }
    }

    @Composable
    fun PremiumTopBar(
        shelterName: String,
        onBackClick: () -> Unit,
        onInfoClick: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            // グラデーション背景
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 戻るボタン
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "戻る",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🧭 AR ナビゲーション",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = shelterName,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }

                // 情報ボタン
                IconButton(
                    onClick = onInfoClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.Blue.copy(alpha = 0.3f),
                            CircleShape
                        )
                        .border(1.dp, Color.Blue.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "詳細情報",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // ステータスインジケーター
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color.Green, CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
        }
    }

    @Composable
    fun ShelterDetailsPanel(
        modifier: Modifier = Modifier,
        onDismiss: () -> Unit
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // ヘッダー
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏛️ 避難所詳細",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "閉じる",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 避難所情報
                DetailItem(
                    icon = Icons.Default.LocationOn,
                    label = "避難所名",
                    value = shelterName,
                    color = Color.Cyan
                )

                DetailItem(
                    icon = Icons.Default.Home,
                    label = "住所",
                    value = shelterAddress,
                    color = Color.Green
                )

                DetailItem(
                    icon = Icons.Default.People,
                    label = "収容人数",
                    value = "${shelterCapacity}人",
                    color = Color.Red
                )

                if (shelterFacilities.isNotEmpty()) {
                    DetailItem(
                        icon = Icons.Default.Build,
                        label = "設備",
                        value = shelterFacilities,
                        color = Color.Yellow
                    )
                }

                if (shelterPhone.isNotEmpty()) {
                    DetailItem(
                        icon = Icons.Default.Phone,
                        label = "電話番号",
                        value = shelterPhone,
                        color = Color.Magenta
                    )
                }

                DetailItem(
                    icon = Icons.Default.DirectionsWalk,
                    label = "徒歩時間",
                    value = "約${getWalkingTime(currentDistance)}分",
                    color = Color.Red
                )
            }
        }
    }

    @Composable
    fun DetailItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        label: String,
        value: String,
        color: Color
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    @Composable
    fun PremiumArOverlay(
        modifier: Modifier = Modifier,
        distance: Double,
        shelterName: String,
        targetBearing: Float,
        currentAzimuth: Double
    ) {
        val angleDifference = normalizeAngle(targetBearing - currentAzimuth.toFloat())
        val isOnTarget = abs(angleDifference) < 15f

        // アニメーション
        val pulseAnimation = rememberInfiniteTransition(label = "pulse")
        val pulseScale by pulseAnimation.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )

        val rotationAnimation = rememberInfiniteTransition(label = "rotation")
        val rotation by rotationAnimation.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing)
            ),
            label = "rotation"
        )

        Box(modifier = modifier) {
            // 🎯 メイン距離表示（中央上部）
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 120.dp)
                    .scale(if (isOnTarget) pulseScale else 1f),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.9f),
                                    Color.Black.copy(alpha = 0.7f)
                                ),
                                radius = 200f
                            ),
                            RoundedCornerShape(24.dp)
                        )
                        .border(
                            2.dp,
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Cyan.copy(alpha = 0.8f),
                                    Color.Blue.copy(alpha = 0.8f)
                                )
                            ),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // アイコン
                        Icon(
                            Icons.Default.GpsFixed,
                            contentDescription = null,
                            tint = Color.Cyan,
                            modifier = Modifier
                                .size(32.dp)
                                .rotate(rotation)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "目的地まで",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = formatDistance(distance),
                            color = Color.Cyan,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = shelterName,
                            color = Color.Yellow,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
            }

            // 🧭 方向指示（画面中央）
            DirectionIndicator(
                modifier = Modifier.align(Alignment.Center),
                angleDifference = angleDifference,
                isOnTarget = isOnTarget
            )

            // 🚶‍♂️ サイド情報パネル
            SideInfoPanels(
                modifier = Modifier.fillMaxSize(),
                distance = distance,
                angleDifference = angleDifference
            )
        }
    }

    @Composable
    fun DirectionIndicator(
        modifier: Modifier = Modifier,
        angleDifference: Float,
        isOnTarget: Boolean
    ) {
        val arrowRotation by animateFloatAsState(
            targetValue = angleDifference,
            animationSpec = tween(300),
            label = "arrowRotation"
        )

        Card(
            modifier = modifier
                .size(120.dp)
                .shadow(16.dp, CircleShape),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = if (isOnTarget) {
                                listOf(
                                    Color.Green.copy(alpha = 0.9f),
                                    Color.Green.copy(alpha = 0.6f)
                                )
                            } else {
                                listOf(
                                    Color.Red.copy(alpha = 0.9f),
                                    Color.Red.copy(alpha = 0.6f)
                                )
                            }
                        ),
                        CircleShape
                    )
                    .border(
                        3.dp,
                        Color.White.copy(alpha = 0.8f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        if (isOnTarget) Icons.Default.CheckCircle else Icons.Default.Navigation,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(40.dp)
                            .rotate(if (isOnTarget) 0f else arrowRotation)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isOnTarget) "正しい方向" else getDirectionText(angleDifference),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    if (!isOnTarget) {
                        Text(
                            text = "${abs(angleDifference).toInt()}°",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun SideInfoPanels(
        modifier: Modifier = Modifier,
        distance: Double,
        angleDifference: Float
    ) {
        Box(modifier = modifier) {
            // 左側：歩行時間
            Card(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(16.dp)
                    .width(80.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Blue.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.DirectionsWalk,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${getWalkingTime(distance)}分",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "歩行",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }
            }

            // 右側：緊急度
            Card(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp)
                    .width(80.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (getUrgencyLevel(distance)) {
                        "高" -> Color.Red.copy(alpha = 0.9f)
                        "中" -> Color(0xFFFFA500).copy(alpha = 0.9f)
                        else -> Color.Green.copy(alpha = 0.9f)
                    }
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        when (getUrgencyLevel(distance)) {
                            "高" -> Icons.Default.Warning
                            "中" -> Icons.Default.Info
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = getUrgencyLevel(distance),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "緊急度",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }

    @Composable
    fun PremiumBottomPanel(
        distance: Double,
        currentAzimuth: Double,
        targetBearing: Double,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.9f),
                                Color.Black.copy(alpha = 0.7f)
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    )
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.3f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // 距離
                        InfoItem(
                            icon = Icons.Default.GpsFixed,
                            label = "距離",
                            value = formatDistance(distance),
                            color = Color.Cyan
                        )

                        // 現在方位
                        InfoItem(
                            icon = Icons.Default.Explore,
                            label = "現在方位",
                            value = "${String.format("%.0f", currentAzimuth)}°",
                            color = Color.Green
                        )

                        // 目標方位
                        InfoItem(
                            icon = Icons.Default.MyLocation,
                            label = "目標方位",
                            value = "${String.format("%.0f", targetBearing)}°",
                            color = Color.Red
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 進行状況バー
                    val progress = if (distance > 1000.0) {
                        0.1f
                    } else {
                        ((1000.0 - distance) / 1000.0).toFloat().coerceIn(0f, 1f)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "避難進行度",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color.Green,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }

    @Composable
    fun InfoItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        label: String,
        value: String,
        color: Color
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp
            )

            Text(
                text = value,
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    @Composable
    fun ErrorScreen(
        hasCameraPermission: Boolean,
        cameraError: String?
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black,
                            Color(0xFF1A1A1A)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        if (!hasCameraPermission) Icons.Default.CameraAlt else Icons.Default.Error,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (!hasCameraPermission) "カメラ権限が必要です" else "カメラエラー",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (cameraError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = cameraError,
                            color = Color.Gray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ArOverlay(
        modifier: Modifier = Modifier,
        currentAzimuth: Double,
        targetBearing: Float,
        distance: Double,
        shelterName: String
    ) {
        Canvas(modifier = modifier) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            // 目標方向への角度差を計算
            val angleDifference = normalizeAngle(targetBearing - currentAzimuth.toFloat())

            // 矢印の位置を計算（画面中央から目標方向へ）
            val arrowDistance = 250f
            val arrowX = centerX + arrowDistance * sin(Math.toRadians(angleDifference.toDouble())).toFloat()
            val arrowY = centerY - arrowDistance * cos(Math.toRadians(angleDifference.toDouble())).toFloat()

            // 大きな方向矢印を描画
            drawDirectionArrow(
                center = Offset(arrowX, arrowY),
                rotation = angleDifference,
                color = if (abs(angleDifference) < 15f) Color.Green else Color.Red,
                size = 80f
            )

            // コンパス表示
            drawCompass(
                center = Offset(centerX, centerY + 250),
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

            // 影効果
            drawPath(
                path,
                Color.Black.copy(alpha = 0.3f),
                style = Stroke(width = 4.dp.toPx())
            )
        }
    }

    private fun DrawScope.drawCompass(
        center: Offset,
        currentAzimuth: Double,
        targetBearing: Float
    ) {
        // コンパスの外枠（グラデーション）
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f),
                    Color.White.copy(alpha = 0.6f)
                )
            ),
            radius = 60f,
            center = center
        )
        drawCircle(
            color = Color.Black.copy(alpha = 0.8f),
            radius = 55f,
            center = center
        )

        // 現在の方位を示す線
        val currentX = center.x + 45 * sin(Math.toRadians(currentAzimuth)).toFloat()
        val currentY = center.y - 45 * cos(Math.toRadians(currentAzimuth)).toFloat()
        drawLine(
            color = Color.Cyan,
            start = center,
            end = Offset(currentX, currentY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )

        // 目標方位を示す線
        val targetX = center.x + 40 * sin(Math.toRadians(targetBearing.toDouble())).toFloat()
        val targetY = center.y - 40 * cos(Math.toRadians(targetBearing.toDouble())).toFloat()
        drawLine(
            color = Color.Red,
            start = center,
            end = Offset(targetX, targetY),
            strokeWidth = 5f,
            cap = StrokeCap.Round
        )

        // 中心点
        drawCircle(
            color = Color.White,
            radius = 8f,
            center = center
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

    // ================== HELPER FUNCTIONS ==================

    private fun formatDistance(distance: Double): String {
        return when {
            distance < 1000 -> "${distance.toInt()}m"
            distance < 10000 -> "${"%.1f".format(distance / 1000)}km"
            else -> "${(distance / 1000).toInt()}km"
        }
    }

    private fun getDirectionText(angleDifference: Float): String {
        return when {
            abs(angleDifference) < 15f -> "直進"
            angleDifference > 45f -> "大きく右へ"
            angleDifference > 15f -> "右へ"
            angleDifference < -45f -> "大きく左へ"
            angleDifference < -15f -> "左へ"
            else -> "微調整"
        }
    }

    private fun getWalkingTime(distance: Double): Int {
        // 歩行速度を時速4kmと仮定
        val walkingSpeedMps = 4000.0 / 60.0 // メートル/分
        return (distance / walkingSpeedMps).toInt()
    }

    private fun getUrgencyLevel(distance: Double): String {
        return when {
            distance > 2000 -> "低"
            distance > 500 -> "中"
            else -> "高"
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
