package com.example.fergietime

data class SettingOption(
    val title: String,
    val description: String? = null,
    val isEnabled: Boolean = true,
    val selectable: Boolean = true,
    val selected: Boolean = false,
    val hasToggle: Boolean = false

)
