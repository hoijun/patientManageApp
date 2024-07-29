package com.example.patientManageApp.presentation.screen.settingUserProfilePage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.patientManageApp.domain.entity.UserEntity
import com.example.patientManageApp.domain.usecase.UpdateUserData
import com.example.patientManageApp.domain.usecase.UseCases
import com.example.patientManageApp.domain.utils.FirebaseApiResult
import com.example.patientManageApp.domain.utils.onError
import com.example.patientManageApp.domain.utils.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingUserViewModel @Inject constructor(private val useCases: UseCases) : ViewModel() {
    private val _settingUserUiState = MutableStateFlow<SettingUserUiState>(SettingUserUiState.Idle)
    val settingUserUiState = _settingUserUiState.asStateFlow()

    fun saveUserProfile(name: String, birth: String, email: String) = viewModelScope.launch(Dispatchers.IO) {
        isLoading()
        useCases.updateUserData(UserEntity(name, birth, email))
            .onSuccess { isSuccess() }
            .onError { isError() }
    }

    private fun isLoading() {
        _settingUserUiState.value = SettingUserUiState.Loading
    }

    private fun isSuccess() {
        _settingUserUiState.value = SettingUserUiState.Success
    }

    private fun isError() {
        _settingUserUiState.value = SettingUserUiState.Error
    }
}