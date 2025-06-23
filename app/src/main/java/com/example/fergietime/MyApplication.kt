package com.example.fergietime

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        val context = LocaleHelper.setLocale(base!!, LocaleHelper.getLanguage(base))
        super.attachBaseContext(context)
    }

    override fun onCreate() {
        super.onCreate()
        LocaleHelper.setLocale(this, LocaleHelper.getLanguage(this))
    }
}
