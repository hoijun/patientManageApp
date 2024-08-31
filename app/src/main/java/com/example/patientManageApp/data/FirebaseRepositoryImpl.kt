package com.example.patientManageApp.data

import android.net.Uri
import android.util.Log
import com.example.patientManageApp.domain.entity.CameraEntity
import com.example.patientManageApp.domain.entity.OccurrencesEntity
import com.example.patientManageApp.domain.entity.PatientEntity
import com.example.patientManageApp.domain.entity.UserEntity
import com.example.patientManageApp.domain.repository.FirebaseRepository
import com.example.patientManageApp.domain.utils.FirebaseApiResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepositoryImpl @Inject constructor(private val db: FirebaseDatabase, private val storage: FirebaseStorage) : FirebaseRepository {
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

    override suspend fun removeUserData(): FirebaseApiResult<Boolean> = try {
        db.getReference("Users").child(Firebase.auth.currentUser!!.uid).removeValue().await()
        FirebaseApiResult.Success(true)
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }

    override suspend fun updateCameraData(cameraEntities: List<CameraEntity>): FirebaseApiResult<Boolean> = try {
        db.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("CameraData").setValue(cameraEntities).await()
        FirebaseApiResult.Success(true)
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }

    override suspend fun getCameraData(): FirebaseApiResult<List<CameraEntity>> = try {
        val cameraEntities =
            db.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("CameraData")
                .get().await().getValue(object : GenericTypeIndicator<List<CameraEntity>>() {}) ?: emptyList()
        FirebaseApiResult.Success(cameraEntities)
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }

    override suspend fun getOccurrenceData(): FirebaseApiResult<HashMap<String, List<OccurrencesEntity>>> = try {
        val isSuccess = CompletableDeferred<Boolean>()
        val occurrenceEntitiesMap = hashMapOf<String, List<OccurrencesEntity>>()
        db.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("OccurrenceData")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(dateData in snapshot.children) {
                            val date = dateData.key ?: "0000-00-00"
                            val occurrencesEntities = mutableListOf<OccurrencesEntity>()
                            for (timeData in dateData.children) {
                                val time = timeData.key ?: "00:00:00"
                                val kind = timeData.child("kind").getValue(String::class.java) ?: ""

                                occurrencesEntities.add(OccurrencesEntity(time, kind))
                            }
                            occurrenceEntitiesMap[date] = occurrencesEntities
                        }
                        isSuccess.complete(true)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        isSuccess.complete(false)
                    }
                })
        if (isSuccess.await()) {
            FirebaseApiResult.Success(occurrenceEntitiesMap)
        } else {
            throw Exception()
        }
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }

    override suspend fun updateAgreeTermOfService(): FirebaseApiResult<Boolean> =  try {
        db.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("agreeTermOfService").setValue(true).await()
        FirebaseApiResult.Success(true)
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }

    override suspend fun updateFcmToken(token: String): FirebaseApiResult<Boolean> = try {
        db.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("fcmToken").setValue(token).await()
        FirebaseApiResult.Success(true)
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }

    override suspend fun getOccurrenceJPG(date: String): FirebaseApiResult<Uri> = try {
        val uri = storage.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("${date}.jpg").downloadUrl.await()
        FirebaseApiResult.Success(uri)
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }

    override suspend fun getOccurrenceMP4(date: String): FirebaseApiResult<Uri> = try {
        val uri = storage.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("${date}.mp4").downloadUrl.await()
        FirebaseApiResult.Success(uri)
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }

    override suspend fun removeOccurrenceJPGAndMP4(dates: HashMap<String, List<OccurrencesEntity>>): FirebaseApiResult<Boolean> = try {
        dates.forEach { (key, value) ->
            value.forEach { occurrencesEntity ->
                val date = key + "_" + occurrencesEntity.time.replace(":", "")
                storage.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("${date}.jpg").delete().await()
                storage.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("${date}.mp4").delete().await()
            }
        }
        FirebaseApiResult.Success(true)
    } catch (e: Exception) {
        FirebaseApiResult.Error(e)
    }
}