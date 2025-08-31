// Top-level build file where you can add configuration options common to all sub-modules/projects.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("com.android.library") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.20" apply false
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
