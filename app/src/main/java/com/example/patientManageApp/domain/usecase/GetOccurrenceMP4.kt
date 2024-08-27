package com.example.patientManageApp.domain.usecase

import android.net.Uri
import com.example.patientManageApp.data.FirebaseRepositoryImpl
import com.example.patientManageApp.domain.repository.FirebaseRepository
import com.example.patientManageApp.domain.utils.FirebaseApiResult

class GetOccurrenceMP4(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke() : FirebaseApiResult<Uri> =
        firebaseRepository.getOccurrenceMP4()
}