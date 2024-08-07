package com.example.patientManageApp.domain.usecase

import android.util.Log
import com.example.patientManageApp.domain.entity.UserEntity
import com.example.patientManageApp.domain.repository.FirebaseRepository

class UpdateUserData(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke(userEntity: UserEntity) =
        firebaseRepository.updateUserData(userEntity)
}