package com.example.fergietime

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fergietime.SettingOption



@Composable

fun SettingOptionCard(
    option: SettingOption,
    onClick: ((SettingOption) -> Unit)? = null,
    onToggleChange: ((SettingOption, Boolean) -> Unit)? = null
) {
    // 既存の isEnabled をローカルに保持（トグル用／必要な場合のみ）
    val enabledState = remember(option) { mutableStateOf(option.isEnabled) }
    val clickable = option.selectable || onClick != null
    val hasToggle: Boolean = false

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (clickable) Modifier.clickable { onClick?.invoke(option) } else Modifier),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                option.description?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            when {
                option.hasToggle -> {
                    Switch(
                        checked = enabledState.value,
                        onCheckedChange = {
                            enabledState.value = it
                            onToggleChange?.invoke(option, it)
                        }
                    )
                }
                // ▼ 追加：言語などの単一選択行で、選択中はチェック表示
                option.selectable && option.selected -> {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
