package com.example.patientManageApp.data

import com.example.patientManageApp.domain.entity.PatientEntity
import com.example.patientManageApp.domain.entity.UserEntity
import com.example.patientManageApp.domain.repository.FirebaseRepository
import com.example.patientManageApp.domain.utils.FirebaseApiResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepositoryImpl @Inject constructor(private val db: FirebaseDatabase) : FirebaseRepository {
    override suspend fun getUserData(): FirebaseApiResult<UserEntity> = try {
        val userData =
            db.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("UserData").get()
                .await().getValue(UserEntity::class.java) ?: UserEntity("", "")
        FirebaseApiResult.Success(userData)
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }

    override suspend fun getPatientData(): FirebaseApiResult<PatientEntity> = try {
        val patientData =
            db.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("PatientData")
                .get()
                .await().getValue(PatientEntity::class.java) ?: PatientEntity("", "")
        FirebaseApiResult.Success(patientData)
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }

    override suspend fun updateUserData(userEntity: UserEntity): FirebaseApiResult<Boolean> = try {
        db.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("UserData").setValue(userEntity).await()
        FirebaseApiResult.Success(true)
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }

    override suspend fun updatePatientData(patientEntity: PatientEntity): FirebaseApiResult<Boolean> = try {
        db.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("PatientData").setValue(patientEntity).await()
        FirebaseApiResult.Success(true)
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }

    override suspend fun removeUserData(): FirebaseApiResult<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateAgreeTermOfService(): FirebaseApiResult<Boolean> =  try {
        db.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("agreeTermOfService").setValue(true).await()
        FirebaseApiResult.Success(true)
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }
}