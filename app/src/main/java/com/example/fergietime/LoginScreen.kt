package com.example.fergietime

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.fergietime.auth.signIn  // Firebaseログイン関数のインポート
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(onBack: () -> Unit,
                onNavigateToRegister: () -> Unit,
                onNavigateToReset: () -> Unit,
                onLoginSuccess: (String) -> Unit) {
    // 入力されたメールアドレス、パスワード、ログインエラーメッセージの状態を保持
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current  // Toastを出すためにContextを取得

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // アプリのロゴ（任意の画像に変更可）
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "ロゴ",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // タイトル「ログイン」
        Text(
            "ログイン",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // メールアドレス入力欄
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("メールアドレス") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // パスワード入力欄（入力内容は非表示に）
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("パスワード") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                val desc = if (passwordVisible) "非表示にする" else "表示する"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = desc)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ログインボタン
        Button(
            onClick = {
                signIn(email, password) { success, message ->
                    if (success) {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null && user.isEmailVerified) {
                            // メール認証済み → Firestore から名前取得して画面遷移
                            val uid = user.uid
                            FirebaseFirestore.getInstance().collection("userdata").document(uid)
                                .get()
                                .addOnSuccessListener { document ->
                                    val name = document.getString("name")
                                    if (name != null) {
                                        onLoginSuccess(name)
                                    } else {
                                        Toast.makeText(context, "名前が見つかりません", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            // 未認証 → エラー表示
                            loginError = "メールアドレスが確認されていません。メールをご確認ください。"
                        }
                    } else {
                        loginError = message ?: "ログインに失敗しました"
                    }
                    }

                },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ログインする", fontWeight = FontWeight.Bold)
        }

        // エラーメッセージがあれば表示
        loginError?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 補助リンク（パスワード忘れなど）
        Text(
            "パスワードをお忘れの方はこちら",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onNavigateToReset() }  // ← 遷移トリガー
                .padding(top = 8.dp)
        )


        // 新規登録画面へのリンク（クリック可能）
        Text(
            "初めての方はこちら",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onNavigateToRegister() }
                .padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 戻るボタン
        OutlinedButton(onClick = onBack) {
            Text("戻る")
        }
    }
}
