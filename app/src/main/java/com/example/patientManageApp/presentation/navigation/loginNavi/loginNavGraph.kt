package com.example.patientManageApp.presentation.navigation.loginNavi

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.patientManageApp.presentation.LoginAppScreen
import com.example.patientManageApp.presentation.moveScreen
import com.example.patientManageApp.presentation.screen.login.loginPage.LoginScreen
import com.example.patientManageApp.presentation.screen.login.loginPage.TermOfServiceScreen
import com.example.patientManageApp.presentation.screen.login.settingProfilePage.SettingProfileProfile

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
            LoginScreen { loginSns ->
                moveScreen(navController, "${LoginAppScreen.TermOfService.route}/${loginSns}")
            }
        }

        composable(route = LoginAppScreen.TermOfService.route + "/{sns}") { backstackEntry ->
            TermOfServiceScreen {
                val loginSns = backstackEntry.arguments?.getString("sns")
                moveScreen(navController, "${LoginAppScreen.UserProfile.route}/${loginSns}")
            }
        }

        composable(route = LoginAppScreen.UserProfile.route + "/{sns}") { backstackEntry ->
            SettingProfileProfile(backstackEntry.arguments?.getString("sns"))
        }
    }
}

private fun getIndexForRoute(route: String): Int {
    return when (route) {
        LoginAppScreen.Login.route -> 0
        LoginAppScreen.TermOfService.route -> 1
        LoginAppScreen.UserProfile.route -> 2
        else -> -1
    }
}