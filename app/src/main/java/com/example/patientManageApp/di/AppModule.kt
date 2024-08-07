package com.example.patientManageApp.di

import com.example.patientManageApp.data.FirebaseRepositoryImpl
import com.example.patientManageApp.domain.repository.FirebaseRepository
import com.example.patientManageApp.domain.usecase.GetPatientData
import com.example.patientManageApp.domain.usecase.GetUserData
import com.example.patientManageApp.domain.usecase.RemoveUserData
import com.example.patientManageApp.domain.usecase.UpdateAgreeTermOfService
import com.example.patientManageApp.domain.usecase.UpdatePatientData
import com.example.patientManageApp.domain.usecase.UpdateUserData
import com.example.patientManageApp.domain.usecase.UseCases
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return Firebase.database
    }

    @Provides
    @Singleton
    fun provideFirebaseRepository(db: FirebaseDatabase): FirebaseRepository = FirebaseRepositoryImpl(db)

    @Provides
    @Singleton
    fun provideUseCases(
        firebaseRepository: FirebaseRepository
    ) = UseCases(
        getUserData = GetUserData(firebaseRepository),
        getPatientData = GetPatientData(firebaseRepository),
        updateUserData = UpdateUserData(firebaseRepository),
        updatePatientData = UpdatePatientData(firebaseRepository),
        removeUserData = RemoveUserData(firebaseRepository),
        updateAgreeTermOfService = UpdateAgreeTermOfService(firebaseRepository)
    )
}