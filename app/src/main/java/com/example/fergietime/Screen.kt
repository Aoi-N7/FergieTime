// Screen.kt
package com.example.fergietime

sealed class Screen {
    object Location : Screen()
    object Notification : Screen()
    object Cache : Screen()
    object Login : Screen()
    object Register : Screen()
    object PasswordReset : Screen()
    data class Home(val userName: String) : Screen() // ← ここを追加
    object Loading : Screen()
}

