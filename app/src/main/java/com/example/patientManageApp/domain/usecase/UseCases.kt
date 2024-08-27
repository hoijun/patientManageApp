package com.example.patientManageApp.domain.usecase

data class UseCases(
    val getUserData: GetUserData,
    val getPatientData: GetPatientData,
    val updateUserData: UpdateUserData,
    val updatePatientData: UpdatePatientData,
    val removeUserData: RemoveUserData,
    val updateCameraData: UpdateCameraData,
    val getCameraData: GetCameraData,
    val getOccurrenceData: GetOccurrenceData,
    val updateAgreeTermOfService: UpdateAgreeTermOfService,
    val updateFcmToken: UpdateFcmToken,
    val getOccurrenceJPG: GetOccurrenceJPG,
    val getOccurrenceMP4: GetOccurrenceMP4
)