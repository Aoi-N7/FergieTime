package com.example.fergietime

import com.google.android.gms.maps.model.LatLng

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

