package com.example.fergietime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.fergietime.SettingOptionCard



data class SettingData(
    val id: String,
    val title: String,
    val options: List<SettingOption>
)

// ▼ 仮のデータ取得関数
fun getSettingData(settingId: String): SettingData? {
    return when (settingId) {
        "language" -> SettingData(
            id = "language",
            title = "言語設定",
            options = listOf(
                SettingOption("日本語", "現在選択中"),
                SettingOption("English"),
                SettingOption("中文"),
                SettingOption("한국어")
            )
        )
        "theme" -> SettingData(
            id = "theme",
            title = "テーマ設定",
            options = listOf(
                SettingOption("ライトテーマ", "明るい背景色を使用"),
                SettingOption("ダークテーマ", "暗い背景色を使用"),
                SettingOption("自動", "システム設定に従う", true, true)
            )
        )
        "voice" -> SettingData(
            id = "voice",
            title = "音声案内設定",
            options = listOf(
                SettingOption("音声案内を有効にする", hasToggle = true, isEnabled = true),
                SettingOption("緊急時のみ音声案内", hasToggle = true, isEnabled = false),
                SettingOption("音量設定", "音声案内の音量を調整"),
                SettingOption("音声の種類", "男性/女性の選択")
            )
        )
        "user" -> SettingData(
            id = "user",
            title = "ユーザー設定",
            options = listOf(
                SettingOption("プロフィール編集", "名前、年齢、連絡先の編集"),
                SettingOption("自宅住所", "現在：神戸市中央区..."),
                SettingOption("勤務先", "現在：未設定"),
                SettingOption("学校", "現在：神戸市立中学校"),
                SettingOption("緊急連絡先", "家族・友人の連絡先登録")
            )
        )
        "family" -> SettingData(
            id = "family",
            title = "家族情報",
            options = listOf(
                SettingOption("家族メンバー追加", "新しい家族メンバーを追加"),
                SettingOption("祖父(山田正男)", "連絡先：090-xxxx-xxxx"),
                SettingOption("母(佐藤美咲)", "連絡先：080-xxxx-xxxx"),
                SettingOption("友人(ラメシュ)", "連絡先：070-xxxx-xxxx"),
                SettingOption("位置情報共有", "家族間での位置情報共有", true, true)
            )
        )
        "notifications" -> SettingData(
            id = "notifications",
            title = "通知設定",
            options = listOf(
                SettingOption("プッシュ通知", "アプリからの通知を受信", true, true),
                SettingOption("緊急警報", "気象警報・避難指示の通知", true, true),
                SettingOption("家族の安否情報", "家族からの安否情報通知", true, true),
                SettingOption("定期確認", "定期的な安否確認通知", true, false),
                SettingOption("通知音", "通知音の設定")
            )
        )
        "cache" -> SettingData(
            id = "cache",
            title = "キャッシュ管理",
            options = listOf(
                SettingOption("キャッシュサイズ", "現在：45.2MB"),
                SettingOption("画像キャッシュ", "地図・写真のキャッシュ：23.1MB"),
                SettingOption("データキャッシュ", "安否情報・設定のキャッシュ：22.1MB"),
                SettingOption("すべてクリア", "すべてのキャッシュを削除")
            )
        )
        "version" -> SettingData(
            id = "version",
            title = "アプリ情報",
            options = listOf(
                SettingOption("バージョン", "v1.0.0"),
                SettingOption("ビルド番号", "20240108"),
                SettingOption("最終更新", "2024年1月8日"),
                SettingOption("利用規約", "利用規約を確認"),
                SettingOption("プライバシーポリシー", "プライバシーポリシーを確認"),
                SettingOption("お問い合わせ", "サポートに連絡")
            )
        )
        else -> null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingDetailScreen(
    selectedSettingId: String?,
    onBack: () -> Unit
) {
    val settingData = selectedSettingId?.let { getSettingData(it) }
    if (settingData == null) {
        onBack()
        return
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isLanguage = settingData.id == "language"

    val options = remember(settingData) {
        mutableStateListOf<SettingOption>().apply { addAll(settingData.options) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(settingData.title, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(options, key = { it.title + (it.languageTag ?: "") }) { option ->
                val idx = options.indexOf(option)
                SettingOptionCard(
                    option = option,
                    onClick = if (isLanguage) { clicked ->
                        val tag = clicked.languageTag
                        if (tag != null) {
                            scope.launch {
                                LanguageManager.setLanguage(context, tag.toString())
                            }

                            val updated = options.mapIndexed { i, o ->
                                if (i == idx) o.copy(selected = true, description = "現在選択中")
                                else o.copy(selected = false, description = null)
                            }
                            options.clear()
                            options.addAll(updated)
                        }
                    } else null
                )
            }
        }
    }
}
