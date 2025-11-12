package com.example.fergietime

// ▼ データクラス定義
data class SettingOption(
    val title: String,
    val description: String? = null,
    val languageTag: Boolean? = null,
    val selectable: Boolean = true,
    val selected: Boolean = false,
    val hasToggle: Boolean = false,
    val isEnabled: Boolean = true
)