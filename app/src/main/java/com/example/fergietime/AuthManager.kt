/**
 * Firebase Authentication を使用してメールアドレスとパスワードで
 * サインインを行うためのユーティリティ関数を提供するファイル。
 *
 * signIn 関数は非同期で認証を実行し、結果をコールバック（onResult）で返す。
 * 成功時には true を、失敗時には false とエラーメッセージを返す。
 */

package com.example.fergietime

import com.google.firebase.auth.FirebaseAuth


// Firebase にメールアドレス + パスワードでサインインする関数。
fun signIn(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
    val auth = FirebaseAuth.getInstance()

    // Firebase Authentication にサインインを要求
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 認証成功時：成功フラグのみ返す
                onResult(true, null)
            } else {
                // 認証失敗時：エラーメッセージを返す
                onResult(false, task.exception?.message)
            }
        }
}
