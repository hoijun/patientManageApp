package com.example.patientManageApp.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.patientManageApp.presentation.navigation.mainNavi.MainBottomNavigation
import com.example.patientManageApp.presentation.navigation.mainNavi.MainNavHost
import com.example.patientManageApp.presentation.screen.main.MainUiState
import com.example.patientManageApp.presentation.screen.main.MainViewModel
import com.example.patientManageApp.presentation.theme.PatientManageAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PatientManageAppTheme {
                Main()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun Main() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val refreshState = rememberPullToRefreshState()

    if (refreshState.isRefreshing) {
        mainViewModel.isIdle()
        refreshState.endRefresh()
    }

    when (mainUiState) {
        is MainUiState.Error -> {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(refreshState.nestedScrollConnection),
                snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
            ) {
                LaunchedEffect(snackBarHostState) {
                    snackBarHostState.showSnackbar(
                        message = "오류가 발생 했습니다.",
                        actionLabel = "닫기",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }

        MainUiState.Loading -> {
            LoadingDialog()
        }

        MainUiState.Idle -> {
            mainViewModel.getUserData()
        }

        MainUiState.Success -> {
            Scaffold(modifier = Modifier
                .fillMaxSize(),
                bottomBar = {
                    MainBottomNavigation(navController = navController)
                }
            ) {
                Box(modifier = Modifier.padding(bottom = it.calculateBottomPadding())) {
                    MainNavHost(
                        navController = navController,
                        startDestination = AppScreen.Home.route,
                        mainViewModel
                    )

                    PullToRefreshContainer(
                        state = refreshState,
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    PatientManageAppTheme {
        Main()
    }
}