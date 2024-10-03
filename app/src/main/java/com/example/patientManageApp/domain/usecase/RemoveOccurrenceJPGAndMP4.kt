package com.example.patientManageApp.domain.usecase

import com.example.patientManageApp.domain.entity.OccurrencesEntity
import com.example.patientManageApp.domain.repository.FirebaseRepository

class RemoveOccurrenceJPGAndMP4(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke() = firebaseRepository.removeOccurrenceJPGAndMP4()
}