package com.example.patientManageApp.presentation.screen.login.loginPage

sealed interface LoginUiState {
    data object IDlE : LoginUiState
    data object SingIn : LoginUiState
    data object SingUp : LoginUiState
    data object LoginError : LoginUiState
    data object IsLoading: LoginUiState
}