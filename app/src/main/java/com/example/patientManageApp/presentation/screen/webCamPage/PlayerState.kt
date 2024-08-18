package com.example.patientManageApp.presentation.screen.webCamPage

sealed class PlayerState {
    data object INITIALIZING : PlayerState()
    data object BUFFERING : PlayerState()
    data object READY : PlayerState()
    data object ERROR : PlayerState()
    data object ENDED : PlayerState()
}