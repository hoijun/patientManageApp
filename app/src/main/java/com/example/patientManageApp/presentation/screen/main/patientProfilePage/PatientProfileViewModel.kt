package com.example.patientManageApp.presentation.screen.main.patientProfilePage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.patientManageApp.domain.entity.PatientEntity
import com.example.patientManageApp.domain.usecase.UseCases
import com.example.patientManageApp.domain.utils.onError
import com.example.patientManageApp.domain.utils.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientProfileViewModel @Inject constructor(private val useCase: UseCases): ViewModel() {
    private val _patientProfileUiState = MutableStateFlow<PatientProfileUiState>(PatientProfileUiState.Idle)
    val patientProfileUiState = _patientProfileUiState.asStateFlow()

    fun updatePatientData(patientEntity: PatientEntity) = viewModelScope.launch {
        isLoading()
        useCase.updatePatientData(patientEntity)
            .onSuccess { isSuccess() }
            .onError { isError() }
    }

    fun isIdle() {
        _patientProfileUiState.value = PatientProfileUiState.Idle

    }

    private fun isLoading() {
        _patientProfileUiState.value = PatientProfileUiState.Loading
    }

    private fun isError() {
        _patientProfileUiState.value = PatientProfileUiState.Error
    }

    private fun isSuccess() {
        _patientProfileUiState.value = PatientProfileUiState.Success
    }
}