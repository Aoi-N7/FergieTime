// 未使用ファイルのため、コメントアウト

//package com.example.fergietime
//
//import com.google.android.gms.maps.model.LatLng
//import com.google.maps.android.PolyUtil
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import org.json.JSONObject
//
//// Google Maps Directions API を使ってルート情報（Polylineや所要時間）を取得するユーティリティクラス
//class MapUtils {
//
//    // HTTP通信を行うためのクライアント（OkHttp）
//    private val client = OkHttpClient()
//
//    /**
//     * Directions API のURLを生成する関数
//     *
//     * @param origin 出発地点の緯度経度
//     * @param destination 到着地点の緯度経度
//     * @param apiKey Google Maps APIキー
//     * @return APIリクエスト用のURL文字列
//     */
//    fun getDirectionsUrl(origin: LatLng, destination: LatLng, apiKey: String): String {
//        return "https://maps.googleapis.com/maps/api/directions/json?" +
//                "origin=${origin.latitude},${origin.longitude}" +
//                "&destination=${destination.latitude},${destination.longitude}" +
//                "&mode=walking" + // 徒歩モードでルートを取得
//                "&key=$apiKey"
//    }
//
//    /**
//     * Directions API を使ってルートのPolyline（経路座標）を取得する関数（非同期）
//     *
//     * @param origin 出発地点
//     * @param destination 到着地点
//     * @param apiKey APIキー
//     * @return Polyline描画用のLatLngリスト
//     */
//    suspend fun fetchRoutePolyline(origin: LatLng, destination: LatLng, apiKey: String): List<LatLng> {
//        return withContext(Dispatchers.IO) {
//            // APIリクエストの作成
//            val request = Request.Builder()
//                .url(getDirectionsUrl(origin, destination, apiKey))
//                .build()
//
//            // リクエストを実行してレスポンスを取得
//            val response = client.newCall(request).execute()
//
//            // レスポンスのJSONをパース
//            val json = JSONObject(response.body?.string() ?: "")
//
//            // overview_polyline からエンコードされた座標文字列を取得
//            val polyline = json.getJSONArray("routes")
//                .getJSONObject(0)
//                .getJSONObject("overview_polyline")
//                .getString("points")
//
//            // PolyUtil を使って座標リストにデコード
//            PolyUtil.decode(polyline)
//        }
//    }
//
//    /**
//     * Directions API を使って所要時間（徒歩）を取得する関数（非同期）
//     *
//     * @param origin 出発地点
//     * @param destination 到着地点
//     * @param apiKey APIキー
//     * @return 所要時間のテキスト（例: "10分"）
//     */
//    suspend fun fetchDuration(origin: LatLng, destination: LatLng, apiKey: String): String {
//        return withContext(Dispatchers.IO) {
//            // APIリクエストの作成
//            val request = Request.Builder()
//                .url(getDirectionsUrl(origin, destination, apiKey))
//                .build()
//
//            // リクエストを実行してレスポンスを取得
//            val response = client.newCall(request).execute()
//
//            // レスポンスのJSONをパース
//            val json = JSONObject(response.body?.string() ?: "")
//
//            // legs 配列から duration オブジェクトを取得し、テキストを返す
//            val legs = json.getJSONArray("routes")
//                .getJSONObject(0)
//                .getJSONArray("legs")
//                .getJSONObject(0)
//
//            legs.getJSONObject("duration").getString("text") // 例: "10分"
//        }
//    }
//}
