package com.example.patientManageApp.domain.usecase

import com.example.patientManageApp.domain.entity.OccurrencesEntity
import com.example.patientManageApp.domain.repository.FirebaseRepository

class RemoveOccurrenceJPGAndMP4(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke(dates: HashMap<String, List<OccurrencesEntity>>) = firebaseRepository.removeOccurrenceJPGAndMP4(dates)
}