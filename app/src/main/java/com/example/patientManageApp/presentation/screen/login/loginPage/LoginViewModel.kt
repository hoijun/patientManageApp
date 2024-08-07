package com.example.patientManageApp.presentation.screen.login.loginPage

import android.content.ContentValues.TAG
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    private val _loginUiState  = MutableStateFlow<LoginUiState>(LoginUiState.IDlE)
    val loginUiState = _loginUiState.asStateFlow()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun googleLogin(activityResult: ActivityResult) {
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
                .getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        loginFail()
                        return@addOnCompleteListener
                    }
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false
                    if (isNewUser) {
                        auth.currentUser?.delete()?.addOnCompleteListener { deleteTask ->
                            if (deleteTask.isSuccessful) {
                                signUp()
                            }
                        }
                    } else {
                        loginSuccess()
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
        _loginUiState.value = LoginUiState.SingIn
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