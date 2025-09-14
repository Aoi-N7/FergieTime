package com.example.disasterapp.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.disasterapp.components.PersonMarker

@Composable
fun MapScreen(
    onPersonClick: (String) -> Unit,
    onNavigateToEvacuation: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            MapContainer(onPersonClick = onPersonClick)
        }
        
        item {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AlertCard()
                EvacuationShelterCard(onNavigateToEvacuation = onNavigateToEvacuation)
            }
        }
    }
}

@Composable
fun MapContainer(onPersonClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawMapBackground(this)
        }
        
        // You (Green) - Top Left
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 48.dp, y = 64.dp)
                .clickable { onPersonClick("you") }
        ) {
            PersonMarker(
                name = "あなた",
                time = "10分前",
                backgroundColor = Color(0xFF4CAF50),
                avatarText = "も"
            )
        }
        
        // Mother (Orange) - Top Right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-64).dp, y = 128.dp)
                .clickable { onPersonClick("mother") }
        ) {
            PersonMarker(
                name = "母",
                time = "29分前",
                backgroundColor = Color(0xFFFF9800),
                avatarText = "母"
            )
        }
        
        // Grandfather (Red) - Bottom Left
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 32.dp, y = (-64).dp)
                .clickable { onPersonClick("grandfather") }
        ) {
            PersonMarker(
                name = "祖父",
                time = "59分前",
                backgroundColor = Color(0xFFF44336),
                avatarText = "祖"
            )
        }
        
        // School Location Marker
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-50).dp)
        ) {
            SchoolMarker()
        }
        
        // Street Labels
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = "神戸市中央区",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun SchoolMarker() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = "神戸市立中学校",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun AlertCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Card(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "祖",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "祖父(山田正男)",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "12時34分",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "救助が来て一人で避難できません。",
                        fontSize = 14.sp,
                        color = Color(0xFFF44336)
                    )
                }
                
                Text(
                    text = "さすがに笑えない状況",
                    fontSize = 14.sp,
                    color = Color(0xFFF44336)
                )
            }
        }
    }
}

@Composable
fun EvacuationShelterCard(onNavigateToEvacuation: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(48.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "●●小学校",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "兵庫県神戸市中央区...",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "ここから \"徒歩7分\"",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            IconButton(
                onClick = onNavigateToEvacuation,
                modifier = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

fun drawMapBackground(drawScope: DrawScope) {
    with(drawScope) {
        val width = size.width
        val height = size.height
        
        val streetColor = Color(0xFFE5E7EB)
        
        // Horizontal Streets
        drawLine(streetColor, Offset(0f, height * 0.2f), Offset(width, height * 0.2f), strokeWidth = 4f)
        drawLine(streetColor, Offset(0f, height * 0.4f), Offset(width, height * 0.4f), strokeWidth = 6f)
        drawLine(streetColor, Offset(0f, height * 0.6f), Offset(width, height * 0.6f), strokeWidth = 4f)
        drawLine(streetColor, Offset(0f, height * 0.8f), Offset(width, height * 0.8f), strokeWidth = 4f)
        
        // Vertical Streets
        drawLine(streetColor, Offset(width * 0.2f, 0f), Offset(width * 0.2f, height), strokeWidth = 4f)
        drawLine(streetColor, Offset(width * 0.4f, 0f), Offset(width * 0.4f, height), strokeWidth = 4f)
        drawLine(streetColor, Offset(width * 0.6f, 0f), Offset(width * 0.6f, height), strokeWidth = 6f)
        drawLine(streetColor, Offset(width * 0.8f, 0f), Offset(width * 0.8f, height), strokeWidth = 4f)
        
        // Building Blocks
        val buildingColor = Color(0xFFF3F4F6)
        
        drawRect(
            color = buildingColor,
            topLeft = Offset(width * 0.05f, height * 0.05f),
            size = androidx.compose.ui.geometry.Size(width * 0.125f, height * 0.125f)
        )
        
        drawRect(
            color = buildingColor,
            topLeft = Offset(width * 0.225f, height * 0.05f),
            size = androidx.compose.ui.geometry.Size(width * 0.15f, height * 0.125f)
        )
    }
}
