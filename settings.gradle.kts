pluginManagement {
    repositories {
        gradlePluginPortal()
        google()       // ✅ Firebaseのプラグイン取得に必要
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()       // ✅ Firebaseのライブラリ取得に必要
        mavenCentral()
    }
}

rootProject.name = "Fergietime"

include(":app") // ← アプリモジュールの名前
