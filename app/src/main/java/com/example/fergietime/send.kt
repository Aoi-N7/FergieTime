package com.example.fergietime

import com.google.firebase.firestore.FirebaseFirestore

// 緯度と経度の値を文字列として Firestore の「user」コレクションに保存する関数
// 引数:
//   latitude: 緯度（Double）
//   longitude: 経度（Double）
fun uploadLocationToFirestore(latitude: Double, longitude: Double) {
    // Firestore のインスタンスを取得
    val db = FirebaseFirestore.getInstance()

    // 緯度と経度をカンマ区切りの文字列に変換（例："35.6895, 139.6917"）
    val locationString = "$latitude, $longitude"

    // 保存するデータをマップ形式で定義（フィールド名は "location"）
    val locationData = mapOf(
        "location" to locationString
    )

    // 「user」コレクションの特定のドキュメント（ID: "gC0zL0w69XW7ywuEMbtW"）にデータを送信
    // merge() を指定することで、他のフィールドを保持したまま "location" フィールドだけ更新
    db.collection("user")
        .document("gC0zL0w69XW7ywuEMbtW")
        .set(locationData, com.google.firebase.firestore.SetOptions.merge())
        .addOnSuccessListener {
            // 成功時のログ出力
            println("Location successfully updated!")
        }
        .addOnFailureListener { e ->
            // 失敗時のログ出力
            println("Error updating location: $e")
        }
}
