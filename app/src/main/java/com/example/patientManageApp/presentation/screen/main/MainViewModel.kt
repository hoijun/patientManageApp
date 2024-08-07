package com.example.patientManageApp.presentation.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.patientManageApp.domain.entity.PatientEntity
import com.example.patientManageApp.domain.entity.UserEntity
import com.example.patientManageApp.domain.usecase.UseCases
import com.example.patientManageApp.domain.utils.getResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel@Inject constructor(private val useCases: UseCases): ViewModel() {
    private val _mainUiState = MutableStateFlow<MainUiState>(MainUiState.Idle)
    val mainUiState = _mainUiState.asStateFlow()

    private val _userData = MutableStateFlow(UserEntity("", ""))
    val userData = _userData.asStateFlow()

    private val _patientData = MutableStateFlow(PatientEntity("", ""))
    val patientData = _patientData.asStateFlow()

    fun getUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading()
            val userResult = CompletableDeferred<Boolean>()
            val patientResult = CompletableDeferred<Boolean>()

            useCases.getUserData().getResult(
                success = {
                    _userData.value = it.data
                    userResult.complete(true)
                },
                error = {
                    _userData.value = UserEntity("", "")
                    userResult.complete(false)
                }
            )

            useCases.getPatientData().getResult(
                success = {
                    _patientData.value = it.data
                    patientResult.complete(true)
                },
                error = {
                    _patientData.value = PatientEntity("", "")
                    patientResult.complete(false)
                }
            )

            if (!userResult.await() && !patientResult.await()) {
                isError()
            } else {
                isSuccess()
            }
        }
    }

    fun updateUserData(userEntity: UserEntity) {
        _userData.value = userEntity
    }

    fun updatePatientData(patientEntity: PatientEntity) {
        _patientData.value = patientEntity
    }

    private fun isLoading() {
        _mainUiState.value = MainUiState.Loading
    }

    private fun isError() {
        _mainUiState.value = MainUiState.Error
    }

    private fun isSuccess() {
        _mainUiState.value = MainUiState.Success
    }
}