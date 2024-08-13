package com.example.patientManageApp.domain.usecase

import com.example.patientManageApp.domain.entity.OccurrencesEntity
import com.example.patientManageApp.domain.repository.FirebaseRepository
import com.example.patientManageApp.domain.utils.FirebaseApiResult

class GetOccurrenceData(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke() : FirebaseApiResult<HashMap<String, List<OccurrencesEntity>>> =
        firebaseRepository.getOccurrenceData()
}