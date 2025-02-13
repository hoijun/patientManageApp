package com.example.patientManageApp.presentation.screen.login.loginPage

import android.util.Log
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.patientManageApp.domain.usecase.UseCases
import com.example.patientManageApp.domain.utils.onError
import com.example.patientManageApp.domain.utils.onSuccess
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(private val useCases: UseCases) : ViewModel() {
    private val _loginUiState  = MutableStateFlow<LoginUiState>(LoginUiState.IDlE)
    val loginUiState = _loginUiState.asStateFlow()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun googleSignIn(result: GetCredentialResponse) {
        try {
            when (val credential = result.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    loginFail()
                                    return@addOnCompleteListener
                                }
                                val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false
                                if (isNewUser) {
                                    auth.currentUser?.delete()
                                        ?.addOnCompleteListener { deleteTask ->
                                            if (deleteTask.isSuccessful) {
                                                signUp()
                                            }
                                        }
                                } else {
                                    loginSuccess()
                                }
                            }
                    }
                }
            }
        } catch (e: Exception) {
            loginFail()
        }
    }

    fun firebaseLogin(sns: String) {
        try {
            if (sns == "kakao") {
                UserApiClient.instance.me { user, error1 ->
                    if (error1 != null) {
                        loginFail()
                    } else if (user?.kakaoAccount?.email != null) {
                        val id = user.kakaoAccount!!.email!!
                        val password = user.id.toString()
                        settingUserAccount(id, password)
                    } else if (user?.kakaoAccount?.email == null) {
                        loginFail()
                    }
                }
            } else if (sns == "naver") {
                val naverProfileCallback = object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        val id = result.profile?.email
                        val password = result.profile?.id
                        if (id == null || password == null) {
                            loginFail()
                            return
                        }
                        settingUserAccount(id, password)
                    }

                    override fun onError(errorCode: Int, message: String) {
                        onFailure(errorCode, message)
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        loginFail()
                    }
                }

                NidOAuthLogin().callProfileApi(naverProfileCallback)
            }
        } catch (e: Exception) {
            loginFail()
        }
    }

    private fun settingUserAccount(id: String, password: String) {
        auth.createUserWithEmailAndPassword(id, password).addOnCompleteListener {
            if (!it.isSuccessful) {
                if (it.exception is FirebaseAuthUserCollisionException) {
                    signIn(id, password)
                } else {
                    loginFail()
                }
            } else {
                auth.currentUser?.delete()?.addOnCompleteListener { deleteTask ->
                    auth.signOut()
                    if (deleteTask.isSuccessful) {
                        signUp()
                    }
                }
            }
        }
    }

    private fun signIn(id: String, password: String) {
        auth.signInWithEmailAndPassword(id, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                loginSuccess()
            } else {
                loginFail()
            }
        }
    }

    private fun loginSuccess() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                loginFail()
                return@addOnCompleteListener
            }

            val token = task.result

            if (token == null) {
                loginFail()
                return@addOnCompleteListener
            }

            viewModelScope.launch {
                useCases.updateFcmToken(token).onSuccess {
                    _loginUiState.value = LoginUiState.SingIn
                }.onError {
                    loginFail()
                }
            }
        }.addOnFailureListener {
            loginFail()
        }
    }

    fun loginFail() {
        _loginUiState.value = LoginUiState.LoginError
    }

    fun isLoading() {
        _loginUiState.value = LoginUiState.IsLoading
    }

    private fun signUp() {
        _loginUiState.value = LoginUiState.SingUp
    }
}