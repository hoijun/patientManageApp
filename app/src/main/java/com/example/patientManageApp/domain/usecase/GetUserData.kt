package com.example.patientManageApp.domain.usecase

import com.example.patientManageApp.domain.entity.UserEntity
import com.example.patientManageApp.domain.repository.FirebaseRepository
import com.example.patientManageApp.domain.utils.FirebaseApiResult

class GetUserData(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke(): FirebaseApiResult<UserEntity> =
        firebaseRepository.getUserData()
}