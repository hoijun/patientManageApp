package com.example.patientManageApp.domain.usecase

import com.example.patientManageApp.domain.entity.CameraEntity
import com.example.patientManageApp.domain.repository.FirebaseRepository

class UpdateCameraData(private val repository: FirebaseRepository) {
    suspend operator fun invoke(cameraEntities: List<CameraEntity>) = repository.updateCameraData(cameraEntities)
}