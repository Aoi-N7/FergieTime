package com.example.fergietime

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import com.example.fergietime.ui.theme.FergieTimeTheme

class MainActivity : ComponentActivity() {

    //
    lateinit var locationSensor: LocationSensor

    override fun attachBaseContext(newBase: Context) {
        val context = LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase))
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAppLogic(this)

        setContent {
            FergieTimeTheme {
                val isLoggedIn = remember { mutableStateOf<Boolean?>(null) }

                LaunchedEffect(Unit) {
                    val user = FirebaseAuth.getInstance().currentUser
                    isLoggedIn.value = user != null
                }

                when (isLoggedIn.value) {
                    null -> {
                        // ローディング中
                        Surface(color = MaterialTheme.colorScheme.background) {
                            CircularProgressIndicator()
                        }
                    }
                    true -> {
                        // ログイン済み → DisasterApp（HomeScreen含む）を表示
                        DisasterApp()
                    }
                    false -> {
                        // 未ログイン → LoginScreen を表示
                        LoginScreen(
                            onBack = {},
                            onNavigateToRegister = {},
                            onNavigateToReset = {},
                            onLoginSuccess = {
                                isLoggedIn.value = true
                            }
                        )
                    }
                }
            }
        }
    }
}
