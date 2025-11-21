package com.example.fergietime

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

/**
 * ユーザーの安否情報を管理するViewModel
 * Firestoreとの読み書きや状態管理を担当
 */
class SafetyStatusViewModel : ViewModel() {

    // 安否状況のテキスト
    var statusText by mutableStateOf("")
        private set

    // 安否状況が登録済みか
    var isRegistered by mutableStateOf(false)
        private set

    // 選択中の安否状況（安全/避難中/危険）
    var selectedStatus by mutableStateOf<String?>(null)
        private set

    // 最終登録時間（文字列表示用）
    var registeredTime by mutableStateOf<String?>(null)
        private set

    // FirestoreとFirebaseAuthのインスタンス
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /** Firestoreから安否状況を読み込む */
    fun loadStatusFromFirestore() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("safetystatus").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Firestoreから取得した情報を反映
                    selectedStatus = document.getString("安否情報")
                    statusText = document.getString("テキスト") ?: ""
                    val timestamp = document.getTimestamp("登録時間")
                    registeredTime = timestamp?.toDate()?.let {
                        SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault()).format(it)
                    }
                    isRegistered = true
                    Log.d("SafetyStatusViewModel", "Firestore読み込み成功: $selectedStatus / $statusText")
                } else {
                    Log.d("SafetyStatusViewModel", "Firestoreにデータなし")
                }
            }
            .addOnFailureListener { e ->
                Log.e("SafetyStatusViewModel", "Firestore読み込み失敗", e)
            }
    }

    /** 安否テキストが変更されたときに呼ばれる */
    fun onStatusTextChange(newText: String) {
        statusText = newText
    }

    /** 安否状況ボタンが押されたときに呼ばれる */
    fun onStatusSelected(status: String) {
        selectedStatus = status
    }

    /** Firestoreに安否情報を登録（または更新）する */
    fun registerStatus() {
        // テキストが空でなく、ステータスが選択されている場合のみ登録
        if (statusText.isNotBlank() && selectedStatus != null) {
            isRegistered = true
            val now = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault()).format(Date())
            registeredTime = now

            val savedStatus = selectedStatus
            val savedText = statusText
            val savedStatusCode = when (selectedStatus) {
                "安全" -> 0
                "避難中" -> 1
                "危険" -> 2
                else -> -1
            }
            val savedTimestamp = Timestamp.now()

            val userId = auth.currentUser?.uid
            if (userId != null) {
                // Firestoreに保存するデータ
                val data = hashMapOf(
                    "安否情報" to savedStatus,
                    "テキスト" to savedText,
                    "登録時間" to savedTimestamp,
                    "statusCode" to savedStatusCode
                )

                db.collection("safetystatus").document(userId)
                    .set(data)
                    .addOnSuccessListener {
                        Log.d("SafetyStatusViewModel", "Firestore上書き成功: userId=$userId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("SafetyStatusViewModel", "Firestore上書き失敗", e)
                    }
            } else {
                Log.e("SafetyStatusViewModel", "Firestore保存失敗: ユーザー未ログイン")
            }
        }
    }
}
