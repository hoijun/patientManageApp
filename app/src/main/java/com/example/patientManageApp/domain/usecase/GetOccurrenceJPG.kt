package com.example.patientManageApp.domain.usecase

import android.net.Uri
import com.example.patientManageApp.domain.repository.FirebaseRepository
import com.example.patientManageApp.domain.utils.FirebaseApiResult

class GetOccurrenceJPG(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke(date: String) : FirebaseApiResult<Uri> =
        firebaseRepository.getOccurrenceJPG(date)
}