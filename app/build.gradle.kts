import java.util.Properties // local.propertiesファイルからプロパティを読み込む

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // MapsSDK
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

    // Firebaseプラグインの追加
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.fergietime"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fergietime"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        // map追記
        val mapsApiKey = rootProject.properties["MAPS_API_KEY"] as? String ?: ""
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey

        // local.properties ファイルからプロパティを読み込む
        val localProperties = Properties()
        localProperties.load(rootProject.file("local.properties").inputStream())

        // build.gradle ファイルに API キーを追加
        buildConfigField("String", "MAPS_API_KEY", "\"${localProperties.getProperty("MAPS_API_KEY")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
        // viewBinding = true // Composeプロジェクトでは通常不要
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// 依存関係
dependencies {
    // --- AndroidX 基本ライブラリ ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose.android) // Navigation Compose
    implementation(libs.androidx.runtime.livedata) // LiveData (もし使うなら)

    // --- Compose BOM (バージョン管理) ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

    // --- Google Play Services ---
    implementation("com.google.android.gms:play-services-location:21.3.0") // ★ 新しいバージョンに更新
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-auth:21.3.0") // 重複を整理

    // --- Google Maps for Compose ---
    implementation("com.google.maps.android:maps-compose:6.1.0")
    implementation("com.google.maps.android:android-maps-utils:2.2.5")

    // --- Firebase BOM (バージョン管理) ---
    implementation(platform("com.google.firebase:firebase-bom:33.1.2")) // ★ 新しいバージョンに更新
    implementation("com.google.firebase:firebase-auth") // -ktxは不要
    implementation("com.google.firebase:firebase-firestore") // -ktxは不要
    implementation("com.google.firebase:firebase-database") // -ktxは不要
    implementation("com.google.firebase:firebase-analytics") // -ktxは不要

    // --- その他 ---
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20240303")

    // --- テスト関連 (重複を整理) ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ARCore
    implementation("com.google.ar:core:1.39.0")

    // CameraX - エミュレーター対応版
    implementation("androidx.camera:camera-core:1.2.3")
    implementation("androidx.camera:camera-camera2:1.2.3")
    implementation("androidx.camera:camera-lifecycle:1.2.3")
    implementation("androidx.camera:camera-view:1.2.3")

    implementation("com.google.android.material:material:1.10.0")
}