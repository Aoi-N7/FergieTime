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

@Composable
fun SettingOptionCard(
    option: SettingOption,
    onClick: ((SettingOption) -> Unit)? = null,
    onToggleChange: ((SettingOption, Boolean) -> Unit)? = null
) {
    // トグル用の状態を保持（必要なときのみ）
    val enabledState = remember(option) { mutableStateOf(option.isEnabled) }
    val clickable = option.selectable || onClick != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (clickable) Modifier.clickable { onClick?.invoke(option) }
                else Modifier
            ),
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
                option.selectable && option.selected -> {
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
