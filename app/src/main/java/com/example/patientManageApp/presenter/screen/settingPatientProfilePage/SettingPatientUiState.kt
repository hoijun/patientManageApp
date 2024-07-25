package com.example.patientManageApp.presenter.screen.settingPatientProfilePage

sealed interface SettingPatientUiState {
    data object Idle: SettingPatientUiState
    data object Loading: SettingPatientUiState
    data object Success: SettingPatientUiState
    data object Error: SettingPatientUiState
}