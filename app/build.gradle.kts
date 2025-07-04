import java.util.Properties     // local.propertiesファイルからプロパティを読み込む

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

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

        // local.propertiesファイルからプロパティを読み込む
        val localProperties = Properties()
        localProperties.load(rootProject.file("local.properties").inputStream())

        // MapboxのアクセストークンをBuildConfigに埋め込む（コード内でBuildConfig.MAPBOX_ACCESS_TOKENとしてアクセス可能）
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"${localProperties.getProperty("MAPBOX_ACCESS_TOKEN")}\"")
        resValue("string", "mapbox_access_token", "\"${localProperties.getProperty("MAPBOX_ACCESS_TOKEN")}\"")

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// 依存関係
dependencies {
    // 初期
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Mapbox
//    implementation("com.mapbox.maps:android:11.0.0") // MapboxのコアSDK
//    implementation("com.mapbox.maps:extension-compose:11.0.0") // Jetpack Compose用の拡張
    implementation("com.mapbox.maps:android:11.13.1")
    // Compose を使用する場合、Compose 拡張機能も追加します。
    implementation("com.mapbox.extension:maps-compose:11.13.1")
}