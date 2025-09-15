package com.example.fergietime

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.disasterapp.screens.*
import com.example.disasterapp.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisasterApp() {
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf("home") }

    val viewModel: SafetyStatusViewModel = viewModel()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onTabSelected = { route ->
                    currentRoute = route
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(
                    onNavigateToEvacuation = {
                        navController.navigate("evacuation")
                    },
                    viewModel = viewModel
                )
            }
            composable("map") {
                val context = LocalContext.current
                val activity = context as? Activity

                activity?.let {
                    MapScreen(
                        activity = it,
                        onPersonClick = { personId ->
                            navController.navigate("person_detail/$personId")
                        },
                        onNavigateToEvacuation = {
                            navController.navigate("evacuation")
                        }
                    )
                }
            }

            composable("safety") {
                SafetyScreen(
                    onPersonClick = { personId ->
                        navController.navigate("person_detail/$personId")
                    },
                    viewModel = viewModel
                )
            }
            composable("settings") {
                SettingsScreen(
                    onSettingClick = { settingId ->
                        navController.navigate("setting_detail/$settingId")
                    }
                )
            }
            composable("evacuation") {
                EvacuationMapScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable("person_detail/{personId}") {
                PersonDetailScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable("setting_detail/{settingId}") {
                SettingDetailScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
