package com.example.patientManageApp.presentation.screen.splashPage

sealed interface SplashUiState {
    data object IDlE : SplashUiState
    data object AutoLogin: SplashUiState
    data object NotAutoLogin: SplashUiState
    data object Error: SplashUiState
}