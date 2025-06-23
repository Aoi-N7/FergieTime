package com.example.fergietime

import android.os.Bundle
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
import com.example.fergietime.ui.theme.FergieTimeTheme

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val context = LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase))
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FergieTimeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    GreetingScreen(
                        modifier = Modifier.padding(padding),
                        onChangeToEnglish = {
                            LocaleHelper.saveLanguage(this, "en")
                            recreate()
                        },
                        onChangeToJapanese = {
                            LocaleHelper.saveLanguage(this, "ja")
                            recreate()
                        }
                    )
                }
            }
        }
    }
}

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
