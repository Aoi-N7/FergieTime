
package com.example.fergietime

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.disasterapp.DisasterApp
import com.example.fergietime.ui.theme.FergieTimeTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            FergieTimeTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    DisasterApp()
//                }
//            }
//        }
//    }

    override fun attachBaseContext(newBase: Context) {
        val context = LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase))
        super.attachBaseContext(context)
    }

    lateinit var locationSensor: LocationSensor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAppLogic(this)
        setContent {

            var isDarkTheme by remember { mutableStateOf(false) }
            FergieTimeTheme(darkTheme = isDarkTheme) {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Loading) }

                LaunchedEffect(Unit) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        FirebaseFirestore.getInstance()
                            .collection("userdata")
                            .document(user.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                val name = document.getString("name")
                                currentScreen = if (name != null) Screen.Home(name) else Screen.Login
                            }
                            .addOnFailureListener {
                                currentScreen = Screen.Login
                            }
                    } else {
                        currentScreen = Screen.Login
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isDarkTheme) Color.Black else Color.White)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { isDarkTheme = !isDarkTheme }) {
                        Text(
                            text = if (isDarkTheme) "ダークテーマが有効" else "ライトテーマが有効",
                            color = if (isDarkTheme) Color.White else Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    val context = LocalContext.current
                    when (currentScreen) {
                        is Screen.Loading -> CircularProgressIndicator()
                        is Screen.Location -> LocationScreen(locationSensor) { currentScreen = it }
                        is Screen.Notification -> NotificationScreen(context) { currentScreen = Screen.Location }
                        is Screen.Cache -> CacheScreen { currentScreen = Screen.Location }
                        is Screen.Login -> LoginScreen(
                            onBack = { currentScreen = Screen.Location },
                            onNavigateToRegister = { currentScreen = Screen.Register },
                            onNavigateToReset = { currentScreen = Screen.PasswordReset },
                            onLoginSuccess = { name -> currentScreen = Screen.Home(name) }
                        )
                        is Screen.Register -> RegisterScreen { currentScreen = Screen.Login }
                        is Screen.PasswordReset -> PasswordResetScreen { currentScreen = Screen.Login }
                        is Screen.Home -> HomeScreen(
                            userName = (currentScreen as Screen.Home).userName,
                            onBack = { currentScreen = Screen.Login },
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                currentScreen = Screen.Login
                            }
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                DisasterApp()
            }

        }
    }
}
