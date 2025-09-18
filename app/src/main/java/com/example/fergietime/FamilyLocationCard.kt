package com.example.fergietime

import androidx.compose.foundation.layout.*
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

@Composable
fun FamilyLocationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "家族の位置",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FamilyMemberStatus(
                    name = "祖父",
                    status = "危険",
                    statusColor = Color(0xFFF44336),
                    avatarText = "祖"
                )
                
                FamilyMemberStatus(
                    name = "母",
                    status = "避難中",
                    statusColor = Color(0xFFFF9800),
                    avatarText = "母"
                )
                
                FamilyMemberStatus(
                    name = "友人",
                    status = "安全",
                    statusColor = Color(0xFF4CAF50),
                    avatarText = "R"
                )
            }
        }
    }
}

@Composable
fun FamilyMemberStatus(
    name: String,
    status: String,
    statusColor: Color,
    avatarText: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.size(40.dp),
            colors = CardDefaults.cardColors(containerColor = statusColor),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarText,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = status,
            fontSize = 10.sp,
            color = statusColor
        )
    }
}
