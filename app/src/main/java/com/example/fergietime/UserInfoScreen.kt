package com.example.fergietime

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Firestoreから既存データをロード
    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        phoneNumber = doc.getString("phoneNumber") ?: ""
                        address = doc.getString("address") ?: ""
                    }
                }

            db.collection("userdata").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        name = doc.getString("name") ?: ""
                    }
                }
        }
    }

    // 電話番号フォーマットチェック（XXX-XXXX-XXXX）
    fun isValidPhoneNumber(number: String): Boolean {
        val regex = Regex("^\\d{3}-\\d{4}-\\d{4}$")
        return regex.matches(number)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ユーザー情報", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 名前（日本語入力可能）
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("名前") },
                modifier = Modifier.fillMaxWidth(),
                //keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
            )

            Spacer(Modifier.height(16.dp))

            // 電話番号（数字とハイフンのみ）
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it.filter { c -> c.isDigit() || c == '-' } },
                label = { Text("電話番号（例：090-1234-5678）") },
                modifier = Modifier.fillMaxWidth(),
                //keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(16.dp))

            // 住所（日本語入力可能）
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("住所") },
                modifier = Modifier.fillMaxWidth(),
                //keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
            )

            Spacer(Modifier.height(24.dp))

            // 保存ボタン
            Button(
                onClick = {
                    if (!isValidPhoneNumber(phoneNumber)) {
                        Toast.makeText(context, "電話番号の形式が正しくありません", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    user?.uid?.let { uid ->
                        val userData = mapOf(
                            "name" to name,
                            "phoneNumber" to phoneNumber,
                            "address" to address
                        )

                        // usersコレクションに保存
                        db.collection("users").document(uid)
                            .set(userData)
                            .addOnSuccessListener {
                                // userdataにも名前を保存
                                db.collection("userdata").document(uid)
                                    .set(mapOf("name" to name))
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "保存しました", Toast.LENGTH_SHORT).show()
                                        onBack()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "userdata更新に失敗しました", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "保存に失敗しました", Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("保存")
            }
        }
    }
}
