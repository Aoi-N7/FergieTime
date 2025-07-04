// Top-level build file where you can add configuration options common to all sub-modules/projects.
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("com.android.library") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false  // 現在のバージョンを維持
}

// プロジェクト全体で使用する変数を定義
extra.apply {
    set("compose_version", "1.5.3")  // 1.9.10と互換性のあるバージョン
    set("kotlin_version", "1.9.10")
    set("compileSdk", 34)
    set("minSdk", 24)
    set("targetSdk", 34)
}

// クリーンタスク
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
