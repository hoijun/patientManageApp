package com.example.patientManageApp.presentation.screen.login.settingProfilePage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.patientManageApp.domain.entity.PatientEntity
import com.example.patientManageApp.domain.entity.UserEntity
import com.example.patientManageApp.domain.usecase.UseCases
import com.example.patientManageApp.domain.utils.onError
import com.example.patientManageApp.domain.utils.onSuccess
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingProfileViewModel @Inject constructor(private val useCases: UseCases) : ViewModel() {
    private val _settingUserUiState =
        MutableStateFlow<SettingProfileUiState>(SettingProfileUiState.Idle)
    private val auth = Firebase.auth
    val settingUserUiState = _settingUserUiState.asStateFlow()

    fun singUp(
        sns: String,
        name: String,
        birth: String,
        patientName: String,
        patientBirth: String
    ) {
        try {
            isLoading()
            if (sns == "kakao") {
                UserApiClient.instance.me { user, error1 ->
                    if (error1 != null) {
                        isError()
                    } else if (user?.kakaoAccount?.email != null) {
                        val id = user.kakaoAccount!!.email!!
                        val password = user.id.toString()
                        settingUserAccount(id, password, name, birth, patientName, patientBirth)
                    } else if (user?.kakaoAccount?.email == null) {
                        isError()
                    }
                }
            } else if (sns == "naver") {
                val naverProfileCallback = object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        val id = result.profile?.email
                        val password = result.profile?.id
                        if (id == null || password == null) {
                            isError()
                            return
                        }
                        settingUserAccount(id, password, name, birth, patientName, patientBirth)
                    }

                    override fun onError(errorCode: Int, message: String) {
                        onFailure(errorCode, message)
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        isError()
                    }
                }

                NidOAuthLogin().callProfileApi(naverProfileCallback)
            }
        } catch (e: Exception) {
            isError()
        }
    }

    fun googleSignUp(
        name: String,
        birth: String,
        patientName: String,
        patientBirth: String,
        googleSignInClient: GoogleSignInClient
    ) {
        try {
            isLoading()
            googleSignInClient.silentSignIn().addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }
                val account = it.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener singUp@{ task ->
                        if (!task.isSuccessful) {
                            isError()
                            return@singUp
                        }
                        val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false
                        if (isNewUser) {
                            saveProfile(name, birth, patientName, patientBirth)
                        }
                    }.addOnFailureListener {
                        isError()
                    }
            }
        } catch (e: Exception) {
            isError()
        }
    }


    private fun settingUserAccount(id: String, password: String, name: String, birth: String, patientName: String, patientBirth: String) {
        auth.createUserWithEmailAndPassword(id, password).addOnCompleteListener {
            if (!it.isSuccessful) {
                isError()
            } else {
                saveProfile(name, birth, patientName, patientBirth)
            }
        }
    }

    private fun saveProfile(name: String, birth: String, patientName: String, patientBirth: String) = viewModelScope.launch(Dispatchers.IO) {
        val userResult = CompletableDeferred<Boolean>()
        val patientResult = CompletableDeferred<Boolean>()
        val termResult = CompletableDeferred<Boolean>()

        useCases.updateUserData(UserEntity(name, birth))
            .onSuccess { userResult.complete(true) }
            .onError { userResult.complete(false) }
        useCases.updatePatientData(PatientEntity(patientName, patientBirth))
            .onSuccess { patientResult.complete(true) }
            .onError { patientResult.complete(false) }
        useCases.updateAgreeTermOfService()
            .onSuccess { termResult.complete(true) }
            .onError { termResult.complete(false) }

        if (!userResult.await() && !patientResult.await() && !termResult.await()) {
            isError()
        } else {
            isSuccess()
        }
    }

    private fun isLoading() {
        _settingUserUiState.value = SettingProfileUiState.Loading
    }

    private fun isSuccess() {
        _settingUserUiState.value = SettingProfileUiState.Success
    }

    private fun isError() {
        _settingUserUiState.value = SettingProfileUiState.Error
    }
}