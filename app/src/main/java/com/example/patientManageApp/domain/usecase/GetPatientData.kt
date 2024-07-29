package com.example.patientManageApp.domain.usecase

import com.example.patientManageApp.domain.entity.PatientEntity
import com.example.patientManageApp.domain.repository.FirebaseRepository
import com.example.patientManageApp.domain.utils.FirebaseApiResult

class GetPatientData(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke() : FirebaseApiResult<PatientEntity> =
        firebaseRepository.getPatientData()
}