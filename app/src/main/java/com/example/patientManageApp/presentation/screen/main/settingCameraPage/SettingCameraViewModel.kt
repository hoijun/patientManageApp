package com.example.patientManageApp.presentation.screen.main.settingCameraPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.patientManageApp.domain.entity.CameraEntity
import com.example.patientManageApp.domain.usecase.UseCases
import com.example.patientManageApp.domain.utils.onError
import com.example.patientManageApp.domain.utils.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingCameraViewModel@Inject constructor(private val useCases: UseCases): ViewModel() {
    private val _settingCameraUiState = MutableStateFlow<SettingCameraUiState>(SettingCameraUiState.Idle)
    val settingCameraUiState = _settingCameraUiState.asStateFlow()

    fun updateCameraData(cameraEntities: List<CameraEntity>) = viewModelScope.launch {
        isLoading()
        useCases.updateCameraData(cameraEntities)
            .onSuccess { isSuccess() }
            .onError { isError() }
    }

    fun isIdle() {
        _settingCameraUiState.value = SettingCameraUiState.Idle
    }

    private fun isSuccess() {
        _settingCameraUiState.value = SettingCameraUiState.Success
    }

    private fun isError() {
        _settingCameraUiState.value = SettingCameraUiState.Error
    }

    private fun isLoading() {
        _settingCameraUiState.value = SettingCameraUiState.Loading
    }
}