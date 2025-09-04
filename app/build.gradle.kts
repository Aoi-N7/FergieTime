import java.util.Properties     // local.propertiesファイルからプロパティを読み込む

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
        compose = false // ← Viewベースに変更
        viewBinding = true // 任意、使いたければ
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
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
    implementation(libs.play.services.maps)
    implementation(libs.material)
    implementation("androidx.appcompat:appcompat:1.6.1") // AppCompat 必須
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Google Play Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // Firebase BOM（これでバージョン統一）
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))

    // Firebase Authentication + Firestore（必要なものだけ）
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Analytics（使っている場合のみ）
    implementation("com.google.firebase:firebase-analytics-ktx")

    implementation("androidx.compose.material:material-icons-extended:1.6.0") // バージョンはComposeに合わせて

    //Google Map API
    implementation("com.google.maps.android:maps-compose:6.1.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.maps.android:android-maps-utils:2.2.5")

    // Google Directions API
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20240303") // JSONパース用


}