package com.example.patientManageApp.presenter.screen.settingUserProfilePage

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
class SettingUserViewModel @Inject constructor() : ViewModel() {
    private val _settingUserUiState = MutableStateFlow<SettingUserUiState>(SettingUserUiState.Idle)
    val settingUserUiState = _settingUserUiState.asStateFlow()

    fun saveUserProfile(name: String, birth: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading()
            delay(1500)
            isSuccess()
        }
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