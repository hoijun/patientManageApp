package com.example.patientManageApp.presentation.navigation.mainNavi

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.patientManageApp.domain.entity.CameraEntity
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
import com.google.gson.Gson

@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: String,
    viewModel: MainViewModel
) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = startDestination,
        route = "main",
        enterTransition = {
            val currentIndex = initialState.destination.route?.let { getIndexForRoute(it.split("/").first())} ?: -1
            val targetIndex = targetState.destination.route?.let { getIndexForRoute(it.split("/").first()) } ?: -1
            if (currentIndex == -1) {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(150))
            } else if (targetIndex == -1) {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(150))
            }
            else if (currentIndex < targetIndex) {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(150))
            } else {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(150))
            }
        },
        exitTransition = {
            val currentIndex = initialState.destination.route?.let { getIndexForRoute(it.split("/").first()) } ?: -1
            val targetIndex = targetState.destination.route?.let { getIndexForRoute(it) } ?: -1
            if (currentIndex == -1) {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(150))
            }
            else if (targetIndex == -1) {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(150))
            }
            else if (targetIndex > currentIndex) {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(150))
            } else {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(150))
            }
        }
    ) {
        composable(route = AppScreen.Home.route) {
            HomeScreen(navController, viewModel)
        }

        composable(route = AppScreen.SettingCamera.route + "/{cameraEntity}") { backstackEntry ->
            val cameraEntityJsonString = backstackEntry.arguments?.getString("cameraEntity")
            val cameraEntity = Gson().fromJson(cameraEntityJsonString, CameraEntity::class.java)
            SettingCameraScreen(navController, cameraEntity, viewModel)
        }

        composable(route = AppScreen.Calendar.route) {
            CalendarScreen(navController = navController, mainViewModel = viewModel)
        }

        composable(route = AppScreen.Calendar.route + "/{date}") {
            val date = it.arguments?.getString("date") ?: "bottom"
            CalendarScreen(navController, date, viewModel)
        }

        composable(route = AppScreen.DetailCalendarInfo.route + "/{dateAndTime}") { backstackEntry ->
            val dateAndTime = backstackEntry.arguments?.getString("dateAndTime") ?: "0000년 00월 00일/00:00"
            DetailCalendarInfoScreen(navController, dateAndTime)
        }

        composable(route = AppScreen.Analysis.route) {
            AnalysisScreen(viewModel)
        }

        composable(route = AppScreen.MyPage.route) {
            MyPageScreen(navController, viewModel)
        }

        composable(route = AppScreen.UserProfile.route) {
            UserProfileScreen(navController, viewModel)
        }

        composable(route = AppScreen.PatientProfile.route) {
            PatientProfileScreen(navController, viewModel)
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