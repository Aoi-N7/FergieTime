package com.example.fergietime

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fergietime.ui.theme.FergieTimeTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FergieTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FirestoreDemo(db)
                }
            }
        }
    }
}

@Composable
fun FirestoreDemo(db: FirebaseFirestore) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("データ未送信") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Cloud Firestore データ入力",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("名前") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("年齢") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val user = hashMapOf(
                "name" to name,
                "age" to age.toIntOrNull()
            )

            db.collection("users")
                .add(user)
                .addOnSuccessListener {
                    result = "保存成功: ${it.id}"
                    Log.d("Firestore", "DocumentSnapshot added with ID: ${it.id}")
                }
                .addOnFailureListener { e ->
                    result = "エラー: ${e.message}"
                    Log.w("Firestore", "Error adding document", e)
                }
        }) {
            Text("Firestoreに保存")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = result)
    }
}
