package com.example.fergietime.ui.theme
// このファイルのパッケージ名を定義（プロジェクト内の名前空間）

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
// 必要なライブラリをインポート（テーマ、カラー、コンテキスト、Compose機能など）

// ダークテーマ用のカラースキームを定義
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,        // メインカラー
    secondary = PurpleGrey80,  // サブカラー
    tertiary = Pink80          // 第三のアクセントカラー
)

// ライトテーマ用のカラースキームを定義
private val LightColorScheme = lightColorScheme(
    primary = Purple40,        // メインカラー
    secondary = PurpleGrey40,  // サブカラー
    tertiary = Pink40          // 第三のアクセントカラー

    /* 他のデフォルトカラーを上書きしたい場合に使用する例（コメントアウト中）
    background = Color(0xFFFFFBFE), // 背景色
    surface = Color(0xFFFFFBFE),    // サーフェス色（カードやパネル）
    onPrimary = Color.White,        // primary 上に表示するテキスト色
    onSecondary = Color.White,      // secondary 上に表示するテキスト色
    onTertiary = Color.White,       // tertiary 上に表示するテキスト色
    onBackground = Color(0xFF1C1B1F), // 背景上に表示するテキスト色
    onSurface = Color(0xFF1C1B1F),    // サーフェス上に表示するテキスト色
    */
)

// アプリ全体のテーマを定義するComposable関数
@Composable
fun FergieTimeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // デフォルトでシステムのダークモード設定を参照
    dynamicColor: Boolean = true,               // Android 12以上なら動的カラーを利用可能
    content: @Composable () -> Unit             // このテーマを適用するUIコンテンツ
) {
    // 適用するカラースキームを決定
    val colorScheme = when {
        // Android 12以上かつ dynamicColor = true の場合はシステムの動的カラーを使用
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        // ダークテーマ指定なら DarkColorScheme
        darkTheme -> DarkColorScheme

        // それ以外は LightColorScheme
        else -> LightColorScheme
    }

    // Material3 のテーマを適用
    MaterialTheme(
        colorScheme = colorScheme, // 選ばれたカラーセット
        typography = Typography,   // フォントスタイル（別ファイルで定義）
        content = content          // テーマを適用するUI部分
    )
}
