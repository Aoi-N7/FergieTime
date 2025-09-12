package com.example.fergietime

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ShelterViewModel : ViewModel() {
    private val _shelters = MutableStateFlow<List<EvacuationShelter>>(emptyList())
    val shelters: StateFlow<List<EvacuationShelter>> = _shelters.asStateFlow()

    init {
        // 仮データ（将来的にFirebaseから取得）
        _shelters.value = listOf(
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
    }
}
