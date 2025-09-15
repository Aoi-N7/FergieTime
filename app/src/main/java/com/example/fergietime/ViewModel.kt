package com.example.fergietime

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun registerStatus() {
        if (statusText.isNotBlank()) {
            isRegistered = true
            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            registeredTime = now.format(formatter)
        }
    }
}
