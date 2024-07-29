package com.example.patientManageApp.presentation.screen.settingPatientProfilePage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingPatientViewModel @Inject constructor(): ViewModel() {
    private val _settingPatientUiState = MutableStateFlow<SettingPatientUiState>(SettingPatientUiState.Idle)
    val settingPatientUiState = _settingPatientUiState.asStateFlow()

    fun savePatientInfo(patientName: String, patientBirth: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading()
            delay(1500)
            isSuccess()
        }
    }

    private fun isLoading() {
        _settingPatientUiState.value = SettingPatientUiState.Loading
    }

    private fun isSuccess() {
        _settingPatientUiState.value = SettingPatientUiState.Success
    }

    private fun isError() {
        _settingPatientUiState.value = SettingPatientUiState.Error
    }
}