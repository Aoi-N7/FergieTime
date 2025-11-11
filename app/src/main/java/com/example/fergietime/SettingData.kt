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


// ▼ データクラス定義
data class SettingOption(
    val title: String,
    val description: String? = null,
    val languageTag: String? = null,
    val selectable: Boolean = true,
    val selected: Boolean = false,
    val hasToggle: Boolean = false,
    val isEnabled: Boolean = true
)


data class SettingData(
    val id: String,
    val title: String,
    val options: List<SettingOption>
)

// ▼ 仮のデータ取得関数
fun getSettingData(id: String): SettingData? {
    val languageOptions = listOf(
        SettingOption("日本語", "現在選択中", "ja", true, true),
        SettingOption("English", null, "en", true, false)
    )
    return when (id) {
        "language" -> SettingData("language", "言語設定", languageOptions)
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
                        val tag = clicked.languageTag ?: return@onClick
                        scope.launch { LanguageManager.setLanguage(context, tag) }

                        val updated = options.mapIndexed { i, o ->
                            if (i == idx) o.copy(selected = true, description = "現在選択中")
                            else o.copy(selected = false, description = null)
                        }
                        options.clear()
                        options.addAll(updated)
                    } else null
                )
            }
        }
    }
}
