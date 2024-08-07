package com.example.patientManageApp.presentation.screen.main.patientProfilePage

sealed interface PatientProfileUiState {
    data object Idle : PatientProfileUiState
    data object Loading : PatientProfileUiState
    data object Success : PatientProfileUiState
    data object Error : PatientProfileUiState
}