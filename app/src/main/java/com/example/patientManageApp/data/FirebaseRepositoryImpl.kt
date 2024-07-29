package com.example.patientManageApp.data

import android.util.Log
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
class FirebaseRepositoryImpl @Inject constructor(db: FirebaseDatabase) : FirebaseRepository {
    private val userRef = db.getReference("Users").child(Firebase.auth.currentUser!!.uid)
    override suspend fun getUserData(): FirebaseApiResult<UserEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getPatientData(): FirebaseApiResult<PatientEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserData(userEntity: UserEntity): FirebaseApiResult<Boolean> = try {
        Log.d("savepoint", userRef.toString())
        userRef.child("UserData").setValue(userEntity).await()
        FirebaseApiResult.Success(true)
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }

    override suspend fun updatePatientData(patientEntity: PatientEntity): FirebaseApiResult<Boolean> = try {
        userRef.child("PatientData").setValue(patientEntity).await()
        FirebaseApiResult.Success(true)
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }
}