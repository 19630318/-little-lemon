package com.dybala.littelemon

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun Navigation(navHostController: NavHostController) {

    val sharedPreferences: SharedPreferences =
        LocalContext.current.getSharedPreferences("MiPref", Context.MODE_PRIVATE)

    val valorBooleano: Boolean = sharedPreferences.getBoolean("Login", false)


    var startDestinations = Onboarding.route

    if (valorBooleano == true){
        startDestinations = Home.route
    } else{
        startDestinations = Onboarding.route
    }

    NavHost(
        navController = navHostController,
        startDestination = startDestinations,
    ){
        composable(route = Onboarding.route){
            Onboarding(navHostController)
        }
        composable(route = Home.route){
            Home(navHostController)
        }
        composable(route = Profile.route){
            Profile(navHostController)
        }
    }
}