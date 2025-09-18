package com.example.fergietime

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

fun registerUser(email: String, password: String, name: String, onResult: (Boolean, String?) -> Unit) {
    // Firebaseの認証とデータベースのインスタンスを取得
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // メールアドレスとパスワードで新規登録を実行
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 登録成功時のユーザー情報を取得
                val user = auth.currentUser

                // メールアドレス確認用のメールを送信
                user?.sendEmailVerification()
                    ?.addOnCompleteListener { verifyTask ->
                        if (verifyTask.isSuccessful) {
                            Log.d("Register", "確認メールを送信しました")
                        } else {
                            Log.e("Register", "確認メール送信に失敗: ${verifyTask.exception?.message}")
                        }
                    }

                // ユーザーIDを取得してFirestoreにユーザー情報を保存
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    val userMap = hashMapOf("name" to name)
                    db.collection("userdata").document(uid).set(userMap)
                        .addOnSuccessListener {
                            // 保存成功時のコールバック
                            onResult(true, null)
                        }
                        .addOnFailureListener { e ->
                            // Firestoreへの保存失敗時のエラーメッセージを返す
                            onResult(false, "Firestoreへの保存失敗: ${e.message}")
                        }
                } else {
                    // UIDが取得できなかった場合のエラー
                    onResult(false, "ユーザーID取得失敗")
                }
            } else {
                // 登録処理に失敗した場合のエラーメッセージを返す
                onResult(false, task.exception?.message)
            }
        }
}
