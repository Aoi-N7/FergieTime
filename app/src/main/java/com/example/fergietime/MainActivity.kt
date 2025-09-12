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
class MainActivity : ComponentActivity() {
    // Googleマップを表示するためのビュー
    var mapView: MapView? = null

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

    // 現在地の情報を保持する変数
    var currentLocation: Location? = null

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
            applicableDisasters = listOf(
                DisasterType.EARTHQUAKE,
                DisasterType.FLOOD,
                DisasterType.LANDSLIDE,
                DisasterType.FIRE
            ),
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
            applicableDisasters = listOf(
                DisasterType.EARTHQUAKE,
                DisasterType.FIRE,
                DisasterType.TSUNAMI
            ),
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
            applicableDisasters = listOf(
                DisasterType.EARTHQUAKE,
                DisasterType.FIRE,
                DisasterType.FLOOD
            ),
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
            applicableDisasters = listOf(
                DisasterType.EARTHQUAKE,
                DisasterType.FIRE,
                DisasterType.FLOOD
            ),
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
            applicableDisasters = listOf(
                DisasterType.EARTHQUAKE,
                DisasterType.FLOOD,
                DisasterType.FIRE
            ),
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
            applicableDisasters = listOf(
                DisasterType.EARTHQUAKE,
                DisasterType.FIRE,
                DisasterType.TSUNAMI
            ),
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
                EvacuationNavApp(shelters = shelters) // UIのメイン部分を呼び出し
            }
        }
    }


}