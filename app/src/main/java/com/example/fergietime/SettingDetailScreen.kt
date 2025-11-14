//package com.example.fergietime
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SettingDetailScreen(
//    selectedSettingId: String?,
//    onBack: () -> Unit
//) {
//    val base = selectedSettingId?.let { getSettingData(it) }
//    if (base == null) {
//        onBack()
//        return
//    }
//
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//
//    val options = remember(base) {
//        mutableStateListOf<SettingOption>().apply { addAll(base.options) }
//    }
//    val isLanguage = base.id == "language"
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = base.title,
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "戻る")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(horizontal = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            items(options, key = { it.title + (it.languageTag ?: "") }) { option ->
//                val idx = options.indexOf(option)
//                SettingOptionCard(
//                    option = option,
//                    onClick = if (isLanguage) { clicked ->
//                        val tag = clicked.languageTag ?: return@SettingOptionCard
//                        // 1) 言語保存 & 即反映
//                        scope.launch { LanguageManager.setLanguage(context, tag) }
//                        // 2) UI選択状態を更新
//                        val updated = options.mapIndexed { i, o ->
//                            if (i == idx) o.copy(selected = true, description = "現在選択中")
//                            else o.copy(selected = false, description = if (o.selectable) null else o.description)
//                        }
//                        options.clear()
//                        options.addAll(updated)
//                    } else null
//                )
//            }
//        }
//    }
//}
