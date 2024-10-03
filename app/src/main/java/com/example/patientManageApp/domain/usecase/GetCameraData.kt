package com.example.patientManageApp.domain.usecase

import com.example.patientManageApp.domain.repository.FirebaseRepository

class GetCameraData(private val repository: FirebaseRepository) {
    suspend operator fun invoke() = repository.getCameraData()
}