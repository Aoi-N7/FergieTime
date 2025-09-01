package com.example.fergietime

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*


@Composable
fun CacheScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 戻るアイコンとタイトル
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                }
                Icon(Icons.Default.Delete, contentDescription = "delete")
                Spacer(modifier = Modifier.width(8.dp))
                Text("キャッシュの削除", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 削除ボタン（赤・大きく・影付き）
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(8.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .defaultMinSize(minWidth = 220.dp, minHeight = 48.dp)
            ) {
                Text("キャッシュの削除", color = Color.Red, fontSize = 18.sp)
            }
        }

        // モーダル（ダイアログ）表示
        if (showDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000)) // 半透明の背景
                    .clickable(enabled = false) {} // 背景クリック無効
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("データをすべて削除します。\nよろしいですか？")

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val success = CacheManager.clearCache(context)
                            Toast.makeText(
                                context,
                                if (success) "削除しました" else "削除できませんでした",
                                Toast.LENGTH_SHORT
                            ).show()
                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("データを削除", color = Color.Red)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { showDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("キャンセル", color = Color.Red)
                    }
                }
            }
        }
    }
}
