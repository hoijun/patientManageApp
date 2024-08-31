package com.example.patientManageApp.presentation.screen.main.userProfilePage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.patientManageApp.domain.entity.OccurrencesEntity
import com.example.patientManageApp.domain.entity.UserEntity
import com.example.patientManageApp.domain.usecase.UseCases
import com.example.patientManageApp.domain.utils.onError
import com.example.patientManageApp.domain.utils.onSuccess
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel@Inject constructor(private val useCase: UseCases): ViewModel() {
    private val _userProfileUiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Idle)
    val userProfileUiState = _userProfileUiState.asStateFlow()

    fun updateUserData(userEntity: UserEntity) = viewModelScope.launch {
        isLoading()
        useCase.updateUserData(userEntity)
            .onSuccess { isUpdateSuccess() }
            .onError { isError() }
    }

    fun logout() = viewModelScope.launch {
        isLoading()
        val auth = Firebase.auth
        val email = auth.currentUser?.email

        if (email == null) {
            isError()
            return@launch
        }

        try {
            if (email.contains("naver.com")) {
                useCase.updateFcmToken("").onSuccess {
                    FirebaseMessaging.getInstance().deleteToken().addOnSuccessListener {
                        NaverIdLoginSDK.logout()
                        auth.signOut()
                        isLogoutSuccess()
                    }.addOnFailureListener {
                        isError()
                    }
                }.onError {
                    isError()
                }
            } else if (email.contains("kakao.com")) {
                UserApiClient.instance.logout kakaoLogin@{ error ->
                    if(error != null) {
                        isError()
                        return@kakaoLogin
                    }
                    viewModelScope.launch updateFcm@ {
                        useCase.updateFcmToken("").onSuccess {
                            FirebaseMessaging.getInstance().deleteToken().addOnSuccessListener {
                                auth.signOut()
                                isLogoutSuccess()
                            }.addOnFailureListener {
                                isError()
                            }
                        }.onError {
                            isError()
                        }
                    }
                }
            } else if (email.contains("gmail.com")) {
                useCase.updateFcmToken("").onSuccess {
                    FirebaseMessaging.getInstance().deleteToken().addOnSuccessListener {
                        auth.signOut()
                        isLogoutSuccess()
                    }.addOnFailureListener {
                        isError()
                    }
                }.onError {
                    isError()
                }
            }
        } catch (e: Exception) {
            isError()
        }
    }

    fun withdrawal(occurrenceData: HashMap<String, List<OccurrencesEntity>>) = viewModelScope.launch {
        isLoading()
        useCase.removeUserData()
            .onSuccess {
                useCase.removeOccurrenceJPGAndMP4(occurrenceData)

                val auth = Firebase.auth
                val email = auth.currentUser?.email

                if (email == null) {
                    isError()
                    return@onSuccess
                }

                if (email.contains("naver.com")) {
                    NidOAuthLogin().callDeleteTokenApi(object : OAuthLoginCallback {
                        override fun onError(errorCode: Int, message: String) {
                            onFailure(errorCode, message)
                        }

                        override fun onFailure(httpStatus: Int, message: String) {
                            isError()
                        }

                        override fun onSuccess() {
                            auth.currentUser!!.delete().addOnSuccessListener {
                                isWithdrawalSuccess()
                            }.addOnFailureListener {
                                isUpdateSuccess()
                            }
                        }
                    })

                } else if (email.contains("kakao.com")) {
                    UserApiClient.instance.unlink { error ->
                        if (error != null) {
                            isError()
                        } else {
                            auth.currentUser!!.delete().addOnSuccessListener {
                                isWithdrawalSuccess()
                            }.addOnFailureListener {
                                isUpdateSuccess()
                            }
                        }
                    }
                } else if (email.contains("gmail.com")) {
                    auth.currentUser!!.delete().addOnSuccessListener {
                        isWithdrawalSuccess()
                    }.addOnFailureListener {
                        isUpdateSuccess()
                    }
                }
            }
            .onError { isError() }
    }

    fun isIdle() {
        _userProfileUiState.value = UserProfileUiState.Idle
    }

    private fun isLoading() {
        _userProfileUiState.value = UserProfileUiState.Loading
    }

    private fun isError() {
        _userProfileUiState.value = UserProfileUiState.Error
    }

    private fun isUpdateSuccess() {
        _userProfileUiState.value = UserProfileUiState.UpdateSuccess
    }

    private fun isLogoutSuccess() {
        _userProfileUiState.value = UserProfileUiState.LogoutSuccess
    }

    private fun isWithdrawalSuccess() {
        _userProfileUiState.value = UserProfileUiState.WithdrawalSuccess
    }
}