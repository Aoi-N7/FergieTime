package com.example.fergietime

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext

import com.google.firebase.auth.FirebaseAuth

@Composable
fun PasswordResetScreen(onBack: () -> Unit) {
    // メールアドレスの状態を保持する変数
    var email by remember { mutableStateOf("") }

    // メッセージを表示するための状態
    var message by remember { mutableStateOf<String?>(null) }

    // コンテキストの取得（必要に応じて使用）
    val context = LocalContext.current

    // 画面全体のレイアウト
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // タイトル表示
        Text("パスワード再設定", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // メールアドレス入力欄
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("登録済みメールアドレス") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 送信ボタン
        Button(
            onClick = {
                // 入力が空かチェック
                if (email.isBlank()) {
                    message = "メールアドレスを入力してください。"
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    // メール形式チェック
                    message = "正しいメールアドレス形式で入力してください。"
                } else {
                    // Firebaseでパスワードリセットメール送信
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                message = "再設定用メールを送信しました。"
                            } else {
                                message = "エラーが発生しました。メールアドレスをご確認ください。"
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            // ボタンのラベル
            Text("パスワード再設定メールを送る")
        }

        // メッセージがあれば表示
        message?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 戻るボタン
        OutlinedButton(onClick = onBack) {
            Text("戻る")
        }
    }
}
