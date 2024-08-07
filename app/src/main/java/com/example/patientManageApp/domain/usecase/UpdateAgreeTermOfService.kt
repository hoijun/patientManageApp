package com.example.patientManageApp.domain.usecase

import com.example.patientManageApp.domain.repository.FirebaseRepository

class UpdateAgreeTermOfService(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke() = firebaseRepository.updateAgreeTermOfService()
}