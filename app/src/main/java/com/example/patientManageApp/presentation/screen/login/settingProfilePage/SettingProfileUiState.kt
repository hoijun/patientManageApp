package com.example.patientManageApp.presentation.screen.login.settingProfilePage

sealed interface SettingProfileUiState {
    data object Idle: SettingProfileUiState
    data object Loading: SettingProfileUiState
    data object Success: SettingProfileUiState
    data object Error: SettingProfileUiState
}