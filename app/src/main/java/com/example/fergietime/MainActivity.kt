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


}