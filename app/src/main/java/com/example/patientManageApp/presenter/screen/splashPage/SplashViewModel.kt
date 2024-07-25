package com.example.patientManageApp.presenter.screen.splashPage

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {
    private val _splashUiState  = MutableStateFlow<SplashUiState>(SplashUiState.IDlE)
    val splashUiState = _splashUiState.asStateFlow()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun checkLoginState() {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                autoLogin()
            } else {
                notAutoLogin()
            }
        } catch (e: Exception) {
            isError()
        }
    }

    private fun autoLogin() {
        _splashUiState.value = SplashUiState.AutoLogin
    }

    private fun notAutoLogin() {
        _splashUiState.value = SplashUiState.NotAutoLogin
    }

    private fun isError() {
        _splashUiState.value = SplashUiState.Error
    }
}