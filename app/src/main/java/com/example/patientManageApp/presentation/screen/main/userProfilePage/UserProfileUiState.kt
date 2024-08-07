package com.example.patientManageApp.presentation.screen.main.userProfilePage

sealed interface UserProfileUiState {
    data object Idle : UserProfileUiState
    data object Loading : UserProfileUiState
    data object UpdateSuccess : UserProfileUiState
    data object LogoutSuccess : UserProfileUiState
    data object WithdrawalSuccess : UserProfileUiState
    data object Error : UserProfileUiState
}