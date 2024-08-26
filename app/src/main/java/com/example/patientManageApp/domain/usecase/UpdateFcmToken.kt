package com.example.patientManageApp.domain.usecase

import com.example.patientManageApp.domain.repository.FirebaseRepository

class UpdateFcmToken(private val repository: FirebaseRepository) {
    suspend operator fun invoke(token: String) = repository.updateFcmToken(token)
}