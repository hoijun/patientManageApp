package com.example.patientManageApp.presentation.navigation.mainNavi

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.patientManageApp.presentation.AppScreen
import com.example.patientManageApp.presentation.screen.main.MainViewModel
import com.example.patientManageApp.presentation.screen.main.analysisPage.AnalysisScreen
import com.example.patientManageApp.presentation.screen.main.calendarPage.CalendarScreen
import com.example.patientManageApp.presentation.screen.main.calendarPage.DetailCalendarInfoScreen
import com.example.patientManageApp.presentation.screen.main.homePage.HomeScreen
import com.example.patientManageApp.presentation.screen.main.myPagePage.MyPageScreen
import com.example.patientManageApp.presentation.screen.main.patientProfilePage.PatientProfileScreen
import com.example.patientManageApp.presentation.screen.main.settingCameraPage.SettingCameraScreen
import com.example.patientManageApp.presentation.screen.main.userProfilePage.UserProfileScreen

@Composable
fun MainNavHost(navController: NavHostController, startDestination: String) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            val currentIndex = initialState.destination.route?.let { getIndexForRoute(it)} ?: -1
            val targetIndex = targetState.destination.route?.let { getIndexForRoute(it) } ?: -1
            if (currentIndex == -1) {
                EnterTransition.None
            }
            else if (targetIndex == -1) {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(250))
            }
            else if (currentIndex < targetIndex) {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(150))
            } else {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(150))
            }
        },
        exitTransition = {
            val currentIndex = initialState.destination.route?.let { getIndexForRoute(it) } ?: -1
            val targetIndex = targetState.destination.route?.let { getIndexForRoute(it) } ?: -1
            if (currentIndex == -1 || targetIndex == -1) {
                ExitTransition.None
            }
            else if (targetIndex > currentIndex) {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(150))
            } else {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(150))
            }
        }
    ) {
        composable(route = AppScreen.Home.route) {
            val viewModel: MainViewModel = hiltViewModel()
            HomeScreen(navController, viewModel)
        }

        composable(route = AppScreen.SettingCamera.route) {
            SettingCameraScreen(navController = navController)
        }

        composable(route = AppScreen.Calendar.route) {
            CalendarScreen(navController)
        }

        composable(route = AppScreen.DetailCalendarInfo.route) {
            DetailCalendarInfoScreen(navController)
        }

        composable(route = AppScreen.Analysis.route) {
            AnalysisScreen()
        }

        composable(route = AppScreen.MyPage.route) {
            MyPageScreen(navController)
        }

        composable(route = AppScreen.UserProfile.route) {
            UserProfileScreen(navController)
        }

        composable(route = AppScreen.PatientProfile.route) {
            PatientProfileScreen(navController)
        }
    }
}

private fun getIndexForRoute(route: String): Int {
    return when (route) {
        AppScreen.Home.route -> 0
        AppScreen.Calendar.route -> 1
        AppScreen.Analysis.route -> 2
        AppScreen.MyPage.route -> 3
        else -> -1
    }
}