package com.example.fergietime

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// Context拡張でDataStoreを簡単に呼び出せるようにする
val Context.settingsDataStore by preferencesDataStore(name = "settings")
