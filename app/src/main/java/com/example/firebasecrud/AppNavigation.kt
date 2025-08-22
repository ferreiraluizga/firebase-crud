package com.example.firebasecrud

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.firebasecrud.pages.HomePage
import com.example.firebasecrud.pages.LoginPage
import com.example.firebasecrud.pages.SignupPage
import com.example.firebasecrud.pages.TasksPage
import com.example.firebasecrud.viewmodel.AuthViewModel
import com.example.firebasecrud.viewmodel.TaskViewModel

@Composable
fun AppNavigation(modifier: Modifier, authViewModel: AuthViewModel, taskViewModel: TaskViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup"){
            SignupPage(modifier, navController, authViewModel)
        }
        composable("home"){
            HomePage(modifier, navController, authViewModel)
        }
        composable("tasks") {
            TasksPage(modifier, navController, taskViewModel)
        }
    })
}
