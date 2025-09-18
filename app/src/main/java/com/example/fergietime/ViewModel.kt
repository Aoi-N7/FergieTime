// ユーザーが選択した状態とメッセージを保持し、登録処理を行う
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

// 安否情報の登録・管理を行う ViewModel クラス
class SafetyStatusViewModel : ViewModel() {
    // ユーザーが入力したメッセージ
    var statusText by mutableStateOf("")
        private set

    // 登録済みかどうかのフラグ
    var isRegistered by mutableStateOf(false)
        private set

    // ユーザーが選択した状態(安全,避難中,危険)
    var selectedStatus by mutableStateOf<String?>(null)
        private set

    // 登録された時刻（HH:mm形式）
    var registeredTime by mutableStateOf<String?>(null)
        private set

    // メッセージの更新時の処理
    fun onStatusTextChange(newText: String) {
        statusText = newText
    }

    // 状態選択時の処理
    fun onStatusSelected(status: String) {
        selectedStatus = status
    }

    // 状態とメッセージを登録する処理
    fun registerStatus() {
        // メッセージが空でなく、状態が選択されている場合のみ登録
        if (statusText.isNotBlank() && selectedStatus != null) {
            isRegistered = true

            // 現在時刻を取得して登録
            val now = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            registeredTime = now

            // ログ出力（確認時以外はコメントアウト）
//            Log.d("SafetyStatusViewModel", "登録完了")
//            Log.d("SafetyStatusViewModel", "状態: $selectedStatus")
//            Log.d("SafetyStatusViewModel", "メッセージ: $statusText")
//            Log.d("SafetyStatusViewModel", "時刻: $registeredTime")
        } else {
            // 状態またはメッセージが未入力の場合のログ
            Log.d("SafetyStatusViewModel", "登録失敗：状態またはメッセージが未入力")
        }
    }
}


