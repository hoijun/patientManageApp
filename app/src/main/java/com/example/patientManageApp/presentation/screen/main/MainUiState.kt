package com.example.patientManageApp.presentation.screen.main

sealed interface MainUiState {
    data object Idle : MainUiState
    data object Loading : MainUiState
    data object Success : MainUiState
    data object Error : MainUiState
}