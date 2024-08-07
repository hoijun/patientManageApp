package com.example.patientManageApp.domain.repository

import com.example.patientManageApp.domain.entity.PatientEntity
import com.example.patientManageApp.domain.entity.UserEntity
import com.example.patientManageApp.domain.utils.FirebaseApiResult

interface FirebaseRepository {
    suspend fun getUserData(): FirebaseApiResult<UserEntity>
    suspend fun getPatientData(): FirebaseApiResult<PatientEntity>
    suspend fun updateUserData(userEntity: UserEntity): FirebaseApiResult<Boolean>
    suspend fun updatePatientData(patientEntity: PatientEntity): FirebaseApiResult<Boolean>
    suspend fun removeUserData(): FirebaseApiResult<Boolean>
    suspend fun updateAgreeTermOfService(): FirebaseApiResult<Boolean>
}