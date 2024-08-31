package com.example.patientManageApp.domain.repository

import android.net.Uri
import com.example.patientManageApp.domain.entity.CameraEntity
import com.example.patientManageApp.domain.entity.OccurrencesEntity
import com.example.patientManageApp.domain.entity.PatientEntity
import com.example.patientManageApp.domain.entity.UserEntity
import com.example.patientManageApp.domain.utils.FirebaseApiResult

interface FirebaseRepository {
    suspend fun getUserData(): FirebaseApiResult<UserEntity>
    suspend fun getPatientData(): FirebaseApiResult<PatientEntity>
    suspend fun updateUserData(userEntity: UserEntity): FirebaseApiResult<Boolean>
    suspend fun updatePatientData(patientEntity: PatientEntity): FirebaseApiResult<Boolean>
    suspend fun removeUserData(): FirebaseApiResult<Boolean>
    suspend fun updateCameraData(cameraEntities: List<CameraEntity>): FirebaseApiResult<Boolean>
    suspend fun getCameraData(): FirebaseApiResult<List<CameraEntity>>
    suspend fun getOccurrenceData(): FirebaseApiResult<HashMap<String, List<OccurrencesEntity>>>
    suspend fun updateAgreeTermOfService(): FirebaseApiResult<Boolean>
    suspend fun updateFcmToken(token: String): FirebaseApiResult<Boolean>
    suspend fun getOccurrenceJPG(date: String) : FirebaseApiResult<Uri>
    suspend fun getOccurrenceMP4(date: String) : FirebaseApiResult<Uri>
    suspend fun removeOccurrenceJPGAndMP4(dates: HashMap<String, List<OccurrencesEntity>>) : FirebaseApiResult<Boolean>
}