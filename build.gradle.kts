// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Firebaseプラグインの追加
    id("com.google.gms.google-services") version "4.4.2" apply false
}

// Google Map API
buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
        classpath("com.google.gms:google-services:4.3.10")
    }
}

// プロジェクト全体で使用する変数を定義
extra.apply {
    set("compose_version", "1.4.6")  // 1.5.3から1.4.6に変更（Kotlin 1.8.20と互換）
    set("kotlin_version", "1.8.20")
    set("compileSdk", 34)
    set("minSdk", 24)
    set("targetSdk", 34)
}

// クリーンタスク
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}