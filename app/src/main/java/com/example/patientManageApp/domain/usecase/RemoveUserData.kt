package com.example.patientManageApp.domain.usecase

import com.example.patientManageApp.domain.repository.FirebaseRepository
import com.example.patientManageApp.domain.utils.FirebaseApiResult

class RemoveUserData(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke(): FirebaseApiResult<Boolean> = firebaseRepository.removeUserData()
}