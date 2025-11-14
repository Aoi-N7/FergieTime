package com.example.fergietime

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.disasterapp.components.PersonCard

@Composable
fun SafetyScreen(
    onPersonClick: (String) -> Unit,
    viewModel: SafetyStatusViewModel
) {
    // 起動時にFirestoreからデータをロード
    LaunchedEffect(Unit) {
        viewModel.loadStatusFromFirestore()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { MySafetyStatusSection(viewModel) }
        item { FamilySection(onPersonClick = onPersonClick) }
        item { FriendsSection(onPersonClick = onPersonClick) }
    }
}

@Composable
fun MySafetyStatusSection(viewModel: SafetyStatusViewModel) {
    val message = if (viewModel.isRegistered) viewModel.statusText else "未入力"
    val time = viewModel.registeredTime ?: "未登録"

    var inputText by remember { mutableStateOf("") }

    Column {
        Text("自分の安否状況", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // 「安全」ボタン（押すとFirestore上書き）
        Button(
            onClick = {
                viewModel.onStatusSelected("安全")
                viewModel.registerStatus()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Card(
                modifier = Modifier.size(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("安全", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$message（$time）",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("状況を入力...") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    viewModel.onStatusTextChange(inputText)
                    viewModel.registerStatus()
                    inputText = ""
                }) {
                    Card(
                        modifier = Modifier.size(32.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FamilySection(onPersonClick: (String) -> Unit) {
    Column {
        Text("家族", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        PersonCard(
            name = "祖父(山田正男)",
            time = "59分前",
            status = "危険",
            statusColor = Color(0xFFF44336),
            message = "救助が来て一人で避難できません。",
            location = "神戸市中央区",
            avatarText = "祖",
            onClick = { onPersonClick("grandfather") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        PersonCard(
            name = "母(山田花子)",
            time = "29分前",
            status = "避難中",
            statusColor = Color(0xFFFF9800),
            message = "現在避難中→○○小学校　徒歩",
            location = "神戸市中央区",
            avatarText = "母",
            onClick = { onPersonClick("mother") }
        )
    }
}

@Composable
fun FriendsSection(onPersonClick: (String) -> Unit) {
    Column {
        Text("友達", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            PersonCard(
                name = "友人(田中太郎)",
                time = "15分前",
                status = "安全",
                statusColor = Color(0xFF4CAF50),
                message = "無事です。避難所にいます。",
                location = "神戸市東灘区",
                avatarText = "R",
                onClick = { onPersonClick("friend") },
                showCard = false
            )
        }
    }
}
