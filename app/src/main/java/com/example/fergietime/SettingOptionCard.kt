package com.example.fergietime

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fergietime.SettingOption

@Composable
fun SettingOptionCard(
    option: SettingOption, // 表示する設定項目データ
    onClick: ((SettingOption) -> Unit)? = null, // 項目クリック時のコールバック
    onToggleChange: ((SettingOption, Boolean) -> Unit)? = null // トグルスイッチ変更時
) {
    // トグル用の状態を Compose 内で保持
    val enabledState = remember(option) { mutableStateOf(option.isEnabled) }

    // クリック可能かどうかの判定
    val clickable = option.selectable || onClick != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            // クリック可能なら Modifier.clickable を追加
            .then(if (clickable) Modifier.clickable { onClick?.invoke(option) } else Modifier),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左側：タイトルと説明
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                // 説明文がある場合は表示
                option.description?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 右側：トグルスイッチまたはチェックマーク
            when {
                option.hasToggle -> {
                    // トグルスイッチ表示
                    Switch(
                        checked = enabledState.value,
                        onCheckedChange = {
                            enabledState.value = it
                            onToggleChange?.invoke(option, it) // 状態変更を外部に通知
                        }
                    )
                }
                option.selectable && option.selected -> {
                    // 言語選択などの単一選択項目で、選択中ならチェックマーク表示
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
