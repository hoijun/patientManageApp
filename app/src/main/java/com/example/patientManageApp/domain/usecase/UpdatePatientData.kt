package com.example.patientManageApp.domain.usecase

import com.example.patientManageApp.domain.entity.PatientEntity
import com.example.patientManageApp.domain.repository.FirebaseRepository

class UpdatePatientData(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke(patientEntity: PatientEntity) =
        firebaseRepository.updatePatientData(patientEntity)
}