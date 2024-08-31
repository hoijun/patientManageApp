package com.example.patientManageApp.presentation.screen.main.detailCalendarInfoPage

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.patientManageApp.domain.usecase.UseCases
import com.example.patientManageApp.domain.utils.getResult
import com.example.patientManageApp.domain.utils.onError
import com.example.patientManageApp.domain.utils.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailCalendarInfoViewModel@Inject constructor(private val useCases: UseCases) : ViewModel() {
    private val _detailCalendarInfoUiState = MutableStateFlow<DetailCalendarInfoUiState>(DetailCalendarInfoUiState.Idle)
    val detailCalendarInfoUiState = _detailCalendarInfoUiState.asStateFlow()

    private val _mp4Uri = MutableStateFlow(Uri.EMPTY)
    val mp4Uri = _mp4Uri.asStateFlow()

    fun getOccurrenceMp4(date: String) = viewModelScope.launch {
        isLoading()
        useCases.getOccurrenceMP4(date).getResult(
            success = {
                _mp4Uri.value = it.data
                isSuccess()
            },
            error = {
                isError()
            }
        )
    }

    fun isIdle() {
        _detailCalendarInfoUiState.value = DetailCalendarInfoUiState.Idle
    }

    private fun isSuccess() {
        _detailCalendarInfoUiState.value = DetailCalendarInfoUiState.Success
    }

    private fun isError() {
        _detailCalendarInfoUiState.value = DetailCalendarInfoUiState.Error
    }

    private fun isLoading() {
        _detailCalendarInfoUiState.value = DetailCalendarInfoUiState.Loading
    }
}