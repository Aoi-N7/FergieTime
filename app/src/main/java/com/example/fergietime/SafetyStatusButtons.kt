package com.example.disasterapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SafetyStatusButtons() {
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    val statuses = listOf(
        Triple("安全", Color(0xFF4CAF50), Icons.Default.Person),
        Triple("避難中", Color(0xFFFF9800), Icons.Default.Person),
        Triple("危険", Color(0xFFF44336), Icons.Default.Person)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        statuses.forEach { (text, color, icon) ->
            val isSelected = selectedStatus == text
            val backgroundColor = if (isSelected) color else Color(0xFFF5F5F5)
            val contentColor = if (isSelected) Color.White else Color.Gray

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { selectedStatus = text },
                    modifier = Modifier.size(64.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = contentColor,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = text,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) color else Color.Gray
                )
            }
        }
    }
}


@Composable
fun SafetyStatusButton(
    text: String,
    color: Color,
    isSelected: Boolean
) {
    val backgroundColor = if (isSelected) color else Color(0xFFF5F5F5)
    val contentColor = if (isSelected) Color.White else Color.Gray
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { },
            modifier = Modifier.size(64.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) color else Color.Gray
        )
    }
}
