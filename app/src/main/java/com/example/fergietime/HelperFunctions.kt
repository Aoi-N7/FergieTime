package com.example.fergietime

fun formatDistance(distance: Double): String {
    return when {
        distance < 1000 -> "${distance.toInt()}m" // 1km未満なら「メートル」で表示
        distance < 10000 -> "${"%.1f".format(distance / 1000)}km" // 10km未満なら小数点1桁の「km」
        else -> "${(distance / 1000).toInt()}km" // それ以上は整数の「km」
    }
}

// 避難所の種類に応じてアイコン（絵文字）を返す関数
fun getShelterTypeIcon(shelterType: ShelterType): String {
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
fun getSiteTypeName(siteType: EvacuationSiteType): String {
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
