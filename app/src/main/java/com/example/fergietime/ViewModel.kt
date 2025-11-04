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

class SafetyStatusViewModel : ViewModel() {
    var statusText by mutableStateOf("")
        private set

    var isRegistered by mutableStateOf(false)
        private set

    var selectedStatus by mutableStateOf<String?>(null)
        private set

    var registeredTime by mutableStateOf<String?>(null)
        private set

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Firestoreからデータを読み込む
    fun loadStatusFromFirestore() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("safetystatus").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
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

    fun onStatusTextChange(newText: String) {
        statusText = newText
    }

    fun onStatusSelected(status: String) {
        selectedStatus = status
    }

    fun registerStatus() {
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
