package com.example.patientManageApp.presentation.screen.main.settingCameraPage

sealed interface SettingCameraUiState {
    data object Idle : SettingCameraUiState
    data object Loading : SettingCameraUiState
    data object Success : SettingCameraUiState
    data object Error : SettingCameraUiState
}