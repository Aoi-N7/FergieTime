package com.example.fergietime

import android.content.Context
import java.io.File

// キャッシュ削除機能を提供するユーティリティクラス
object CacheManager {

    // アプリのキャッシュを削除する関数（成功：true、失敗：false を返す）
    fun clearCache(context: Context): Boolean {
        return try {
            // キャッシュディレクトリの取得（/data/data/パッケージ名/cache）
            val cacheDir: File = context.cacheDir

            // 再帰的にキャッシュフォルダを削除
            deleteDir(cacheDir)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // 指定されたディレクトリまたはファイルを再帰的に削除する関数
    private fun deleteDir(dir: File?): Boolean {
        // ディレクトリかつ null でない場合
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            if (children != null) {
                for (child in children) {
                    // 子要素に対しても同様に deleteDir を呼び出す（再帰処理）
                    val success = deleteDir(File(dir, child))
                    if (!success) {
                        return false
                    }
                }
            }
        }

        // 最終的にファイルまたは空ディレクトリを削除
        return dir?.delete() ?: false
    }
}
