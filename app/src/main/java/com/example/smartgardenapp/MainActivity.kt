package com.example.smartgardenapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartgarden.DashboardScreen
import com.example.smartgarden.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val mainViewModel: MainViewModel = viewModel()

            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(viewModel = mainViewModel, navController = navController)
                }
                composable("dashboard") {
                    DashboardScreen(viewModel = mainViewModel)
                }
            }
        }
    }
}