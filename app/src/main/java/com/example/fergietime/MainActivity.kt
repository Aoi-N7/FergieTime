package com.example.fergietime

import android.graphics.Color
import android.os.Bundle
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.content.Context
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.example.fergietime.ui.theme.FergieTimeTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val context = LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase))
        super.attachBaseContext(context)
    }

    // 位置情報取得用のクラス
    lateinit var locationSensor: LocationSensor

    private var isDarkTheme = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAppLogic(this)
        setContent {
            FergieTimeTheme {
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

                when (currentScreen) {
                    is Screen.Loading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

                    is Screen.Location -> LocationScreen(locationSensor) { currentScreen = it }
                    is Screen.Notification -> NotificationScreen(this) { currentScreen = Screen.Location }
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
    }
}

/*
class MainActivity : AppCompatActivity() {

    private var isDarkTheme = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layout = findViewById<ConstraintLayout>(R.id.rootLayout)
        val textView = findViewById<TextView>(R.id.textView)
        val button = findViewById<Button>(R.id.buttonSwitchTheme)

        button.setOnClickListener {
            isDarkTheme = !isDarkTheme

            if (isDarkTheme) {
                textView.text = "ダークテーマが有効"
                layout.setBackgroundColor(Color.BLACK)
                textView.setTextColor(Color.WHITE)
                button.setBackgroundColor(Color.DKGRAY)
                button.setTextColor(Color.WHITE)
            } else {
                textView.text = "ライトテーマが有効"
                layout.setBackgroundColor(Color.WHITE)
                textView.setTextColor(Color.BLACK)
                button.setBackgroundColor(Color.LTGRAY)
                button.setTextColor(Color.BLACK)
            }
        }
    }
}
*/

@Composable
fun GreetingScreen(
    modifier: Modifier = Modifier,
    onChangeToEnglish: () -> Unit,
    onChangeToJapanese: () -> Unit
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(text = stringResource(id = R.string.greeting))
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onChangeToEnglish() }) {
            Text(text = stringResource(id = R.string.change_language))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onChangeToJapanese() }) {
            Text(text = stringResource(id = R.string.change_language_japanese))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FergieTimeTheme {
        GreetingScreen(
            onChangeToEnglish = {},
            onChangeToJapanese = {}
        )
    }
}