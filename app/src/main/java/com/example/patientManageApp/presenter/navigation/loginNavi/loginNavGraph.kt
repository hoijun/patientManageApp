package com.example.patientManageApp.presenter.navigation.loginNavi

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.patientManageApp.presenter.LoginAppScreen
import com.example.patientManageApp.presenter.moveScreen
import com.example.patientManageApp.presenter.screen.loginPage.LoginScreen
import com.example.patientManageApp.presenter.screen.settingPatientProfilePage.SettingPatientProfile
import com.example.patientManageApp.presenter.screen.settingUserProfilePage.SettingUserProfile

@Composable
fun LoginNavHost(navController: NavHostController, startDestination: String) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            val currentIndex = initialState.destination.route?.let { getIndexForRoute(it)} ?: -1
            val targetIndex = targetState.destination.route?.let { getIndexForRoute(it) } ?: -1

            if (currentIndex < targetIndex) {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(100))
            } else {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(100))
            }
        },
        exitTransition = {
            val currentIndex = initialState.destination.route?.let { getIndexForRoute(it) } ?: -1
            val targetIndex = targetState.destination.route?.let { getIndexForRoute(it) } ?: -1

            if (targetIndex > currentIndex) {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(100))
            } else {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(100))
            }
        }
    ) {
        composable(route = LoginAppScreen.Login.route) {
            LoginScreen {
                moveScreen(navController, LoginAppScreen.UserProfile.route)
            }
        }

        composable(route = LoginAppScreen.UserProfile.route) {
            SettingUserProfile {
                moveScreen(navController, LoginAppScreen.PatientProfile.route)
            }
        }

        composable(route = LoginAppScreen.PatientProfile.route) {
            SettingPatientProfile()
        }
    }
}

private fun getIndexForRoute(route: String): Int {
    return when (route) {
        LoginAppScreen.Login.route -> 0
        LoginAppScreen.UserProfile.route -> 1
        LoginAppScreen.PatientProfile.route -> 2
        else -> -1
    }
}