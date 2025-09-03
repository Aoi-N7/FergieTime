package com.example.fergietime
// アプリのパッケージ名を定義

import android.Manifest
// 位置情報権限などを扱うための定数を提供するクラス

import android.content.Context
// Androidアプリ全般のContext（アプリ情報、リソースアクセスなどに使う）をインポート

import android.content.Intent
// 別のアクティビティやサービスを起動するためのIntentクラスをインポート

import android.content.pm.PackageManager
// 権限のチェックに必要なPackageManagerをインポート

import android.location.Location
// 緯度・経度・高度など位置情報を扱うクラス

import android.os.Bundle
// アクティビティの状態を保存・復元するためのBundleクラス

import android.util.Log
// デバッグ用のログ出力に使用するクラス

import android.widget.Toast
// 画面下に短時間メッセージを表示するためのクラス

import androidx.activity.ComponentActivity
// Jetpack Composeに対応した基本のActivityクラス

import androidx.activity.compose.rememberLauncherForActivityResult
// 権限リクエストなどの結果を受け取るためのCompose API

import androidx.activity.compose.setContent
// ComposeのUIをActivityにセットするための関数

import androidx.activity.result.contract.ActivityResultContracts
// 権限リクエストなど標準的なActivityResult契約クラスを提供

import androidx.compose.foundation.clickable
// UI要素をクリック可能にするための修飾子

import androidx.compose.foundation.layout.*
// レイアウト用のCompose関数（ColumnやRowなど）

import androidx.compose.foundation.lazy.LazyColumn
// リストを効率的に表示するための縦スクロール用コンポーネント

import androidx.compose.foundation.lazy.items
// LazyColumn内でリスト要素を繰り返し表示するための関数

import androidx.compose.material.icons.Icons
// 標準で提供されるMaterialアイコンセットを扱う

import androidx.compose.material.icons.filled.LocationOn
// ロケーションアイコンを利用するため

import androidx.compose.material.icons.filled.Navigation
// ナビゲーションアイコンを利用するため

import androidx.compose.material3.*
// Material Design 3のUIコンポーネント一式

import androidx.compose.runtime.*
// Composeで状態（State）を管理するため

import androidx.compose.ui.Alignment
// レイアウト内の要素配置用

import androidx.compose.ui.Modifier
// UIの見た目や配置を修飾するためのModifierクラス

import androidx.compose.ui.graphics.Color
// 色を扱うクラス

import androidx.compose.ui.platform.LocalContext
// Compose内で現在のContextを取得するため

import androidx.compose.ui.text.font.FontWeight
// テキストの太さを指定するため

import androidx.compose.ui.unit.dp
// 単位dpを使うため

import androidx.compose.ui.unit.sp
// 単位spを使うため（フォントサイズ用）

import androidx.compose.ui.viewinterop.AndroidView
// 既存のAndroidビュー（MapViewなど）をCompose内に埋め込むためのAPI

import androidx.core.app.ActivityCompat
// 権限のチェックやリクエストを補助するクラス

import com.google.android.gms.common.ConnectionResult
// Google Play Servicesの接続結果を表すクラス

import com.google.android.gms.common.GoogleApiAvailability
// Google Play Servicesの利用可能性を確認するクラス

import com.google.android.gms.location.LocationServices
// 位置情報取得用のFusedLocationProviderClientを使うため

import com.google.android.gms.maps.CameraUpdateFactory
// マップのカメラ位置やズーム操作を行うクラス

import com.google.android.gms.maps.GoogleMap
// Googleマップを操作するためのメインクラス

import com.google.android.gms.maps.GoogleMapOptions
// GoogleMapの初期設定を指定するクラス

import com.google.android.gms.maps.MapView
// Googleマップを表示するためのビュー

import com.google.android.gms.maps.model.*
// マーカーやポリライン、LatLngなどマップ上のオブジェクトを扱う

import kotlin.math.*
// 三角関数や平方根を計算する標準ライブラリ

// ================== DATA CLASSES ==================

// 避難場所の種類を表す列挙型
enum class ShelterType {
    ELEMENTARY_SCHOOL,   // 小学校
    MIDDLE_SCHOOL,       // 中学校
    HIGH_SCHOOL,         // 高校
    COMMUNITY_CENTER,    // 公民館
    GYMNASIUM,           // 体育館
    PARK,                // 公園
    OTHER                // その他
}

// 避難場所の詳細な種類を表す列挙型
enum class EvacuationSiteType {
    DESIGNATED_EMERGENCY_EVACUATION_SITE,  // 指定緊急避難場所
    DESIGNATED_EVACUATION_SHELTER,         // 指定避難所
    TSUNAMI_EVACUATION_BUILDING,           // 津波避難ビル
    WIDE_AREA_EVACUATION_SITE,             // 広域避難場所
    TEMPORARY_EVACUATION_SITE,             // 一時避難場所
    WELFARE_EVACUATION_SHELTER             // 福祉避難所
}

// 災害の種類を表す列挙型
enum class DisasterType {
    FLOOD,          // 洪水
    LANDSLIDE,      // 土砂災害
    HIGH_TIDE,      // 高潮
    EARTHQUAKE,     // 地震
    TSUNAMI,        // 津波
    FIRE,           // 火災
    INLAND_FLOOD    // 内水氾濫
}

// 避難所のデータをまとめるクラス
data class EvacuationShelter(
    val id: String,                          // 識別子
    val name: String,                        // 避難所の名称
    val address: String,                     // 住所
    val position: LatLng,                    // 緯度・経度
    val capacity: Int,                       // 収容人数
    val shelterType: ShelterType = ShelterType.OTHER,      // 種別（小学校、公園など）
    val siteType: EvacuationSiteType = EvacuationSiteType.DESIGNATED_EVACUATION_SHELTER, // 詳細種別
    val applicableDisasters: List<DisasterType> = listOf(DisasterType.EARTHQUAKE),      // 対応可能な災害
    val facilities: List<String> = emptyList(),   // 利用できる設備（トイレ、給水など）
    val phoneNumber: String? = null,              // 連絡先電話番号（任意）
    val isBarrierFree: Boolean = false,           // バリアフリー対応かどうか
    val hasPetSupport: Boolean = false,           // ペット同伴可能かどうか
    val prefecture: String = "",                  // 都道府県
    val city: String = "",                        // 市区町村
    val ward: String? = null,                     // 区（任意）
    val isOpen: Boolean = true,                   // 避難所が開設中かどうか
    val isOpen24Hours: Boolean = true,            // 24時間利用可能かどうか
    val notes: String? = null,                    // 備考
    val distance: Float = 0f                      // 現在地からの距離（初期は0）
)

// 避難所と距離をセットにしたデータクラス
data class ShelterWithDistance(
    val shelter: EvacuationShelter,   // 避難所の情報
    val distance: Double              // 現在地からの距離
)

class MainActivity : ComponentActivity() {
    // Googleマップを表示するためのビュー
    private var mapView: MapView? = null
    // 現在地の情報を保持する変数
    private var currentLocation: Location? = null

    // ================== SHELTER DATA ==================

    // 避難所データ（サンプルとして神戸市の三ノ宮周辺の避難所情報をハードコーディング）
    private val shelters = listOf(
        // 兵庫県神戸市三ノ宮駅周辺
        EvacuationShelter(
            id = "kobe_001",
            name = "神戸市立中央小学校",
            address = "兵庫県神戸市中央区中山手通4-23-2",
            position = LatLng(34.6937, 135.1955),
            capacity = 600,
            shelterType = ShelterType.ELEMENTARY_SCHOOL,
            siteType = EvacuationSiteType.DESIGNATED_EVACUATION_SHELTER,
            applicableDisasters = listOf(DisasterType.EARTHQUAKE, DisasterType.FLOOD, DisasterType.LANDSLIDE, DisasterType.FIRE),
            facilities = listOf("体育館", "校庭", "教室", "給水設備", "非常用電源", "医務室"),
            phoneNumber = "078-221-4768",
            isBarrierFree = true,
            prefecture = "兵庫県",
            city = "神戸市",
            ward = "中央区"
        ),
        EvacuationShelter(
            id = "kobe_002",
            name = "東遊園地",
            address = "兵庫県神戸市中央区加納町6-4-1",
            position = LatLng(34.6851, 135.1947),
            capacity = 2000,
            shelterType = ShelterType.PARK,
            siteType = EvacuationSiteType.WIDE_AREA_EVACUATION_SITE,
            applicableDisasters = listOf(DisasterType.EARTHQUAKE, DisasterType.FIRE, DisasterType.TSUNAMI),
            facilities = listOf("広場", "トイレ", "水道", "防災倉庫", "ヘリポート"),
            isBarrierFree = true,
            hasPetSupport = true,
            prefecture = "兵庫県",
            city = "神戸市",
            ward = "中央区",
            notes = "広域避難場所・ヘリポート利用可能"
        ),
        EvacuationShelter(
            id = "kobe_003",
            name = "神戸市役所",
            address = "兵庫県神戸市中央区加納町6-5-1",
            position = LatLng(34.6851, 135.1956),
            capacity = 800,
            shelterType = ShelterType.OTHER,
            siteType = EvacuationSiteType.DESIGNATED_EVACUATION_SHELTER,
            applicableDisasters = listOf(DisasterType.EARTHQUAKE, DisasterType.FIRE, DisasterType.FLOOD),
            facilities = listOf("災害対策本部", "会議室", "非常用電源", "通信設備", "給水設備"),
            phoneNumber = "078-331-8181",
            isBarrierFree = true,
            prefecture = "兵庫県",
            city = "神戸市",
            ward = "中央区",
            notes = "災害対策本部設置場所"
        ),
        EvacuationShelter(
            id = "kobe_004",
            name = "兵庫県公館",
            address = "兵庫県神戸市中央区下山手通4-4-1",
            position = LatLng(34.6918, 135.1889),
            capacity = 300,
            shelterType = ShelterType.OTHER,
            siteType = EvacuationSiteType.DESIGNATED_EVACUATION_SHELTER,
            applicableDisasters = listOf(DisasterType.EARTHQUAKE, DisasterType.FIRE),
            facilities = listOf("会議室", "ホール", "非常用電源", "給水設備"),
            phoneNumber = "078-341-7711",
            isBarrierFree = true,
            prefecture = "兵庫県",
            city = "神戸市",
            ward = "中央区"
        ),
        EvacuationShelter(
            id = "kobe_005",
            name = "神戸国際会館",
            address = "兵庫県神戸市中央区御幸通8-1-6",
            position = LatLng(34.6919, 135.1975),
            capacity = 1200,
            shelterType = ShelterType.OTHER,
            siteType = EvacuationSiteType.DESIGNATED_EVACUATION_SHELTER,
            applicableDisasters = listOf(DisasterType.EARTHQUAKE, DisasterType.FIRE, DisasterType.FLOOD),
            facilities = listOf("大ホール", "会議室", "レストラン", "非常用電源", "給水設備"),
            phoneNumber = "078-230-3300",
            isBarrierFree = true,
            prefecture = "兵庫県",
            city = "神戸市",
            ward = "中央区"
        ),
        EvacuationShelter(
            id = "kobe_006",
            name = "生田神社",
            address = "兵庫県神戸市中央区下山手通1-2-1",
            position = LatLng(34.6919, 135.1947),
            capacity = 400,
            shelterType = ShelterType.OTHER,
            siteType = EvacuationSiteType.TEMPORARY_EVACUATION_SITE,
            applicableDisasters = listOf(DisasterType.EARTHQUAKE, DisasterType.FIRE),
            facilities = listOf("境内", "社務所", "トイレ", "水道"),
            phoneNumber = "078-321-3851",
            isBarrierFree = false,
            hasPetSupport = true,
            prefecture = "兵庫県",
            city = "神戸市",
            ward = "中央区",
            notes = "一時避難場所として利用"
        ),
        EvacuationShelter(
            id = "kobe_007",
            name = "神戸市立葺合高等学校",
            address = "兵庫県神戸市中央区野崎通1-1-1",
            position = LatLng(34.6889, 135.2019),
            capacity = 700,
            shelterType = ShelterType.HIGH_SCHOOL,
            siteType = EvacuationSiteType.DESIGNATED_EVACUATION_SHELTER,
            applicableDisasters = listOf(DisasterType.EARTHQUAKE, DisasterType.FLOOD, DisasterType.FIRE),
            facilities = listOf("体育館", "校庭", "教室", "給水設備", "非常用電源", "医務室"),
            phoneNumber = "078-291-0771",
            isBarrierFree = true,
            prefecture = "兵庫県",
            city = "神戸市",
            ward = "中央区"
        ),
        EvacuationShelter(
            id = "kobe_008",
            name = "HAT神戸・なぎさ公園",
            address = "兵庫県神戸市中央区脇浜海岸通1-3",
            position = LatLng(34.7056, 135.2167),
            capacity = 2500,
            shelterType = ShelterType.PARK,
            siteType = EvacuationSiteType.WIDE_AREA_EVACUATION_SITE,
            applicableDisasters = listOf(DisasterType.EARTHQUAKE, DisasterType.FIRE, DisasterType.TSUNAMI),
            facilities = listOf("広場", "防災施設", "ヘリポート", "給水設備", "トイレ", "防災倉庫"),
            isBarrierFree = true,
            hasPetSupport = true,
            prefecture = "兵庫県",
            city = "神戸市",
            ward = "中央区",
            notes = "広域避難場所・津波避難可能・ヘリポート利用可能"
        )
    )

    // アクティビティのライフサイクル開始時に呼ばれる
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Jetpack Composeを使ってUIを描画
        setContent {
            MaterialTheme { // Material Design 3 テーマ適用
                EvacuationNavApp() // UIのメイン部分を呼び出し
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class) // 実験的APIを利用しているため明示
    @Composable
    fun EvacuationNavApp() {
        val context = LocalContext.current // Contextを取得
        var googleMapRef by remember { mutableStateOf<GoogleMap?>(null) } // GoogleMapの参照を保持
        var selectedShelter by remember { mutableStateOf<EvacuationShelter?>(null) } // 選択された避難所
        var showShelterList by remember { mutableStateOf(false) } // 避難所リストの表示状態
        var currentLocationState by remember { mutableStateOf<Location?>(null) } // 現在地を保持

        // 現在地から距離を計算して避難所を並べ替える
        val sortedShelters by remember {
            derivedStateOf {
                currentLocationState?.let { location ->
                    shelters.map { shelter ->
                        ShelterWithDistance(
                            shelter = shelter,
                            distance = calculateDistance(
                                location.latitude, location.longitude,
                                shelter.position.latitude, shelter.position.longitude
                            )
                        )
                    }.sortedBy { it.distance } // 距離の昇順でソート
                } ?: shelters.map { ShelterWithDistance(it, Double.MAX_VALUE) } // 現在地がない場合は距離不明として扱う
            }
        }


        // 位置情報の権限をリクエストするランチャー
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // 権限が許可された場合 → 現在地と避難所を地図に表示
                googleMapRef?.let { map ->
                    showLocationAndShelters(context, map) { location ->
                        currentLocationState = location
                    }
                }
            } else {
                // 権限が拒否された場合
                Toast.makeText(context, "位置情報の権限が必要です", Toast.LENGTH_LONG).show()
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // ヘッダー部分
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xffd1e0f9)) // 背景色
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "避難ARナビ", // タイトル
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "最寄りの避難所を確認してください", // サブタイトル
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    // 現在地が取得できている場合に表示
                    currentLocationState?.let {
                        Text(
                            text = "📍 現在地取得済み",
                            fontSize = 12.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // マップビューを配置するコンテナ
            Box(
                modifier = Modifier
                    .fillMaxWidth() // 横幅いっぱいに広げる
                    .weight(1f)     // 余ったスペースを占有（上のヘッダーとのバランス調整）
            ) {
                // Google Map を表示するためのAndroidネイティブビューをComposeに埋め込む
                AndroidView(
                    factory = { ctx -> // MapView を生成する処理
                        MapView(ctx, GoogleMapOptions()).apply {
                            onCreate(null) // MapViewの初期化
                            mapView = this // 作成したMapViewを保持
                            getMapAsync { map -> // 非同期でGoogleMapのインスタンスを取得
                                googleMapRef = map // 参照を保存
                                setupMap(map) { shelter -> // 地図の初期設定（マーカー設置など）
                                    selectedShelter = shelter // マーカーを選択したら避難所を更新
                                }

                                if (hasLocationPermission(context)) {
                                    // 位置情報の権限がある場合 → 現在地と避難所を表示
                                    showLocationAndShelters(context, map) { location ->
                                        currentLocationState = location
                                    }
                                } else {
                                    // 権限がない場合 → ユーザーにリクエスト
                                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize(), // MapViewを画面いっぱいに表示
                    update = { mapView -> // 再描画時に呼ばれる処理
                        mapView.onStart()  // MapViewを再開
                        mapView.onResume() // MapViewを前面に表示状態に
                    }
                )

                // 右下に配置するフローティングボタン群
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // Boxの右下に配置
                        .padding(16.dp)             // 画面端から少し余白
                ) {
                    // 避難所一覧を開くボタン
                    FloatingActionButton(
                        onClick = { showShelterList = !showShelterList }, // 押すと一覧の表示/非表示を切り替える
                        containerColor = MaterialTheme.colorScheme.primary, // テーマカラー
                        modifier = Modifier.padding(bottom = 8.dp) // 下に余白
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "避難所一覧")
                    }

                    // 避難所が選択されている場合にだけ表示するボタン
                    selectedShelter?.let { shelter ->
                        FloatingActionButton(
                            onClick = {
                                // ナビゲーション開始処理（ARナビへ遷移するイメージ）
                                showShelterInfo(shelter, currentLocationState)
                            },
                            containerColor = Color(0xFF38A169) // 緑色
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = "ARナビ開始")
                        }
                    }
                }
            }

// ================= 避難所一覧表示部分 =================

// フラグが true のときに表示される（リストの開閉）
            if (showShelterList) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth() // 横幅いっぱい
                        .height(300.dp) // 高さ固定
                        .padding(8.dp)  // 外側の余白
                ) {
                    Column {
                        // リストのヘッダー部分
                        Text(
                            text = if (currentLocationState != null) "📍 距離順（近い順）" else "避難所一覧",
                            fontWeight = FontWeight.Bold, // 太字
                            fontSize = 16.sp,             // 少し大きめ文字
                            modifier = Modifier.padding(12.dp),
                            color = if (currentLocationState != null) Color(0xFF38A169) else Color.Gray
                        )

                        // スクロール可能なリスト
                        LazyColumn(
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            // 距離順に並んだ避難所リストを表示
                            items(sortedShelters) { shelterWithDistance ->
                                ShelterListItem(
                                    shelter = shelterWithDistance.shelter, // 避難所データ
                                    distance = if (shelterWithDistance.distance != Double.MAX_VALUE)
                                        shelterWithDistance.distance else null, // 距離が分かるなら表示
                                    isNearby = shelterWithDistance.distance < 1000, // 1km以内なら「近い」として扱う
                                    onClick = {
                                        // リストのアイテムをタップしたとき
                                        selectedShelter = shelterWithDistance.shelter
                                        googleMapRef?.animateCamera(
                                            CameraUpdateFactory.newLatLngZoom(
                                                shelterWithDistance.shelter.position, 16f // 地図をその避難所にズーム
                                            )
                                        )
                                        showShelterList = false // リストを閉じる
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ShelterListItem(
        shelter: EvacuationShelter,
        distance: Double?,
        isNearby: Boolean = false,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = if (isNearby) Color(0xFFE6FFFA) else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isNearby) 4.dp else 2.dp
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${getShelterTypeIcon(shelter.shelterType)} ${shelter.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (isNearby) {
                            Text(
                                text = " 🔥",
                                fontSize = 16.sp
                            )
                        }
                    }
                    Text(
                        text = shelter.address,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "収容人数: ${shelter.capacity}人 | ${getSiteTypeName(shelter.siteType)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    if (shelter.facilities.isNotEmpty()) {
                        Text(
                            text = "設備: ${shelter.facilities.take(3).joinToString(", ")}",
                            fontSize = 11.sp,
                            color = Color.Blue
                        )
                    }
                }
                // 距離表示
                distance?.let {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = formatDistance(it),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNearby) Color(0xFF38A169) else Color.Blue
                        )
                        if (isNearby) {
                            Text(
                                text = "近い",
                                fontSize = 10.sp,
                                color = Color(0xFF38A169)
                            )
                        }
                    }
                }
            }
        }
    }

    // ================== HELPER FUNCTIONS ==================

    // 距離を人間にわかりやすい形式に変換する関数
    private fun formatDistance(distance: Double): String {
        return when {
            distance < 1000 -> "${distance.toInt()}m" // 1km未満なら「メートル」で表示
            distance < 10000 -> "${"%.1f".format(distance / 1000)}km" // 10km未満なら小数点1桁の「km」
            else -> "${(distance / 1000).toInt()}km" // それ以上は整数の「km」
        }
    }

    // 避難所の種類に応じてアイコン（絵文字）を返す関数
    private fun getShelterTypeIcon(shelterType: ShelterType): String {
        return when (shelterType) {
            ShelterType.ELEMENTARY_SCHOOL -> "🏫" // 小学校
            ShelterType.MIDDLE_SCHOOL -> "🏫"    // 中学校
            ShelterType.HIGH_SCHOOL -> "🏫"      // 高校
            ShelterType.COMMUNITY_CENTER -> "🏢" // 公民館
            ShelterType.GYMNASIUM -> "🏟️"       // 体育館
            ShelterType.PARK -> "🏞️"             // 公園
            ShelterType.OTHER -> "🏛️"           // その他（公共施設など）
        }
    }

    // 避難所の「区分」に応じて正式名称を返す関数
    private fun getSiteTypeName(siteType: EvacuationSiteType): String {
        return when (siteType) {
            EvacuationSiteType.DESIGNATED_EMERGENCY_EVACUATION_SITE -> "指定緊急避難場所"
            EvacuationSiteType.DESIGNATED_EVACUATION_SHELTER -> "指定避難所"
            EvacuationSiteType.TSUNAMI_EVACUATION_BUILDING -> "津波避難ビル"
            EvacuationSiteType.WIDE_AREA_EVACUATION_SITE -> "広域避難場所"
            EvacuationSiteType.TEMPORARY_EVACUATION_SITE -> "一時避難場所"
            EvacuationSiteType.WELFARE_EVACUATION_SHELTER -> "福祉避難所"
        }
    }

    // GoogleMapを初期設定し、避難所マーカーを追加する関数
    private fun setupMap(googleMap: GoogleMap, onMarkerClick: (EvacuationShelter) -> Unit) {
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
    private fun showLocationAndShelters(
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
                    currentLocation = location
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
                        currentLocation = location
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
    private fun showShelterInfo(shelter: EvacuationShelter, currentLoc: Location?) {
        currentLoc?.let { location -> // 現在地がある場合
            // 現在地と避難所の距離を計算
            val distance = calculateDistance(
                location.latitude, location.longitude,
                shelter.position.latitude, shelter.position.longitude
            )

            // ARナビゲーション画面に遷移するIntentを作成
            val intent = Intent(this, ArNavigationActivity::class.java).apply {
                putExtra("shelter_name", shelter.name) // 避難所名
                putExtra("shelter_lat", shelter.position.latitude) // 避難所の緯度
                putExtra("shelter_lng", shelter.position.longitude) // 避難所の経度
                putExtra("user_lat", location.latitude) // ユーザーの緯度
                putExtra("user_lng", location.longitude) // ユーザーの経度
                putExtra("shelter_capacity", shelter.capacity) // 収容人数
                putExtra("shelter_facilities", shelter.facilities.joinToString(", ")) // 設備
                putExtra("shelter_phone", shelter.phoneNumber ?: "") // 電話番号（nullの場合は空文字）
                putExtra("shelter_address", shelter.address) // 住所
            }
            startActivity(intent) // ARナビ画面を開始
        } ?: run {
            // 現在地が取得できない場合は、避難所情報をToastで表示
            Toast.makeText(
                this,
                "${shelter.name}\n${shelter.address}\n収容人数: ${shelter.capacity}人\n設備: ${shelter.facilities.joinToString(", ")}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // 2地点の緯度経度から距離（メートル）を計算（ハーサイン公式）
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
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
    private fun hasLocationPermission(context: Context): Boolean {
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

    // MapView のライフサイクルをActivityのライフサイクルに同期させる
    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapView?.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

}

