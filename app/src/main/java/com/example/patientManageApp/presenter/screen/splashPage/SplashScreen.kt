package com.example.patientManageApp.presenter.screen.splashPage

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.patientManageApp.LoginActivity
import com.example.patientManageApp.MainActivity

@Composable
fun SplashScreen(viewModel: SplashViewModel = hiltViewModel()) {
    val splashState: SplashUiState by viewModel.splashUiState.collectAsState()
    val context = LocalContext.current

    when(splashState) {
        SplashUiState.AutoLogin -> {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        SplashUiState.NotAutoLogin -> {
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        SplashUiState.IDlE -> {

        }
    }

    viewModel.checkLoginState()
}