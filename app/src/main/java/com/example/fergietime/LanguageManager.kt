package com.example.fergietime

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object LanguageManager {
    private val KEY_LANG = stringPreferencesKey("app_language")

    /** 保存済みの言語タグを監視（未設定なら空文字） */
    fun languageFlow(context: Context): Flow<String> =
        context.settingsDataStore.data.map { prefs -> prefs[KEY_LANG].orEmpty() }

    /** 言語タグを保存し、アプリに即適用（"ja" / "en" / "zh" / "ko" など） */
    suspend fun setLanguage(context: Context, languageTag: String) {
        // DataStoreに保存
        context.settingsDataStore.edit { it[KEY_LANG] = languageTag }
        // すぐ反映（再起動不要）
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageTag)
        )
    }
}
