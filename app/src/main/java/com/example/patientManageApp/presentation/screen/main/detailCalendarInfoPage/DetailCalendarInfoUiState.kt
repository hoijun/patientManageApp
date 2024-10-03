package com.example.patientManageApp.presentation.screen.main.detailCalendarInfoPage

import com.example.patientManageApp.presentation.screen.main.settingCameraPage.SettingCameraUiState

sealed class DetailCalendarInfoUiState {
    data object Idle : DetailCalendarInfoUiState()
    data object Loading : DetailCalendarInfoUiState()
    data object Success : DetailCalendarInfoUiState()
    data object Error : DetailCalendarInfoUiState()
}