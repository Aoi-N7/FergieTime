package com.example.fergietime

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import android.util.Log
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

    fun onStatusTextChange(newText: String) {
        statusText = newText
    }

    fun onStatusSelected(status: String) {
        selectedStatus = status
    }

    fun registerStatus() {
        if (statusText.isNotBlank() && selectedStatus != null) {
            isRegistered = true

            val now = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            registeredTime = now

            // ✅ ログ出力
            Log.d("SafetyStatusViewModel", "登録完了")
            Log.d("SafetyStatusViewModel", "状態: $selectedStatus")
            Log.d("SafetyStatusViewModel", "メッセージ: $statusText")
            Log.d("SafetyStatusViewModel", "時刻: $registeredTime")
        } else {
            Log.d("SafetyStatusViewModel", "登録失敗：状態またはメッセージが未入力")
        }
    }
}

