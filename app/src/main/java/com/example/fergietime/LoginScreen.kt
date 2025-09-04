package com.example.fergietime

// 必要なライブラリやCompose、Firebaseのインポート
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fergietime.auth.signIn // Firebaseログイン用関数
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(
    onBack: () -> Unit, // 「戻る」ボタン押下時の遷移処理
    onNavigateToRegister: () -> Unit, // 新規登録画面への遷移
    onNavigateToReset: () -> Unit, // パスワードリセット画面への遷移
    onLoginSuccess: (String) -> Unit // ログイン成功後に渡される名前
) {
    // ユーザー入力情報を保持する状態（ステート）
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) } // パスワードの表示/非表示切り替え
    var errorMessage by remember { mutableStateOf<String?>(null) } // エラーメッセージの状態

    val context = LocalContext.current // Toast表示のためのContext取得

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // アプリロゴの表示
        Image(
            painter = painterResource(id = R.drawable.sinnromaru),
            contentDescription = "ロゴ",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ログインタイトル
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
            isError = errorMessage != null && email.isBlank(), // 空欄チェック
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // パスワード入力欄（可視切り替え可能）
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("パスワード") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                // パスワード表示/非表示アイコン切り替え
                val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                val desc = if (passwordVisible) "非表示にする" else "表示する"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = desc)
                }
            },
            isError = errorMessage != null && password.length < 6,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ログインボタン
        Button(
            onClick = {
                // 入力チェック（バリデーション）
                errorMessage = when {
                    email.isBlank() || password.isBlank() ->
                        "すべての項目を入力してください。"
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                        "正しいメールアドレスの形式で入力してください。"
                    password.length < 6 ->
                        "パスワードは6文字以上で入力してください。"
                    else -> null
                }

                // 入力が正しければFirebaseにログイン処理
                if (errorMessage == null) {
                    signIn(email, password) { success, message ->
                        if (success) {
                            val user = FirebaseAuth.getInstance().currentUser
                            // メール認証確認
                            if (user != null && user.isEmailVerified) {
                                val uid = user.uid
                                // Firestore からユーザー名取得
                                FirebaseFirestore.getInstance().collection("userdata").document(uid)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        val name = document.getString("name")
                                        if (name != null) {
                                            onLoginSuccess(name) // 成功時に名前を返して画面遷移
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "名前が見つかりません",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                // メール認証が未完了
                                errorMessage = "メールアドレスが確認されていません。メールをご確認ください。"
                            }
                        } else {
                            // ログイン処理失敗時
                            errorMessage = message ?: "ログインに失敗しました"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ログインする", fontWeight = FontWeight.Bold)
        }

        // エラーメッセージ表示エリア（表示条件付き）
        errorMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // パスワードを忘れたときのリンク
        Text(
            "パスワードをお忘れの方はこちら",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onNavigateToReset() } // パスワードリセット画面へ
                .padding(top = 8.dp)
        )

        // 新規登録画面へのリンク
        Text(
            "初めての方はこちら",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onNavigateToRegister() } // 登録画面へ遷移
                .padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 戻るボタン
        OutlinedButton(onClick = onBack) {
            Text("戻る")
        }
    }
}
