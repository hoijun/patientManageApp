package com.example.patientManageApp.presentation.screen.login.loginPage

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.patientManageApp.BuildConfig
import com.example.patientManageApp.R
import com.example.patientManageApp.presentation.BackOnPressed
import com.example.patientManageApp.presentation.LoadingDialog
import com.example.patientManageApp.presentation.MainActivity
import com.example.patientManageApp.presentation.ScreenHeader
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(), movePage: (sns: String) -> Unit) {
    BackOnPressed()

    var snsLoginState by remember { mutableStateOf("") }

    val loginState: LoginUiState by viewModel.loginUiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    when (loginState) {
        LoginUiState.SingIn -> {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        LoginUiState.LoginError -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
            ) {
                LaunchedEffect(snackBarHostState) {
                    snackBarHostState.showSnackbar(
                        message = "로그인에 실패했습니다.",
                        actionLabel = "닫기",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }

        LoginUiState.SingUp -> {
            movePage(snsLoginState)
        }

        LoginUiState.IsLoading -> {
            LoadingDialog()
        }

        LoginUiState.IDlE -> {}
    }

    LoginScreen(
        onKakaoLogin = {
            kakaoLogin(context, viewModel)
            snsLoginState = "kakao"
        },
        onNaverLogin = {
            naverLogin(context, viewModel)
            snsLoginState = "naver"
        },
        onGoogleLogin = {
            googleLogin(context, coroutineScope, viewModel)
            snsLoginState = "google"
        }
    )
}

@Composable
fun LoginScreen(
    onKakaoLogin: () -> Unit,
    onNaverLogin: () -> Unit,
    onGoogleLogin: () -> Unit)
{
    Column {
        ScreenHeader(pageName = "로그인")
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Text(
                    text = "소셜 로그인으로",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                )

                Text(
                    text = "간단한 로그인",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .padding(top = 5.dp)
                )

                SnsLoginButton(
                    "카카오로 시작하기",
                    Color(0xffFEE500),
                    painterResource(id = R.drawable.kakao_symbol)
                ) {
                    onKakaoLogin()
                }
                SnsLoginButton(
                    "네이버로 시작하기",
                    Color(0xff03c75a),
                    painterResource(id = R.drawable.naver_symbol)
                ) {
                    onNaverLogin()
                }
                GoogleLoginButton {
                    onGoogleLogin()
                }
            }
        }
    }
}

@Composable
private fun SnsLoginButton(text: String, color: Color, icon: Painter, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(color),
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp, top = 50.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Image(
                painter = icon,
                contentDescription = text,
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = text,
                modifier = Modifier
                    .weight(1f)
                    .padding(),
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GoogleLoginButton(onClick: () -> Unit) {
    var openGoogleLoginDialog by remember { mutableStateOf(false) }
    if (openGoogleLoginDialog) {
        AlertDialog(
            onDismissRequest = { openGoogleLoginDialog = false },
            title = { Text("안내") },
            text = { Text("구글 계정이 존재 하지 않거나, 5개 이상 등록 되어 있을 경우 로그인이 불가능 합니다.") },
            containerColor = Color(0xFFE2E2E2),
            confirmButton = {
                TextButton(
                    onClick = {
                        openGoogleLoginDialog = false
                        onClick()  // 확인 버튼 클릭시 원래의 onClick 실행
                    }
                ) {
                    Text("확인", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openGoogleLoginDialog = false }
                ) {
                    Text("취소", color = Color.Black)
                }
            }
        )
    }

    Button(
        onClick = { openGoogleLoginDialog = true },
        colors = ButtonDefaults.buttonColors(Color(0xff4285F4)),
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp, top = 50.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_symbol),
                contentDescription = "google",
                modifier = Modifier
                    .size(30.dp)
                    .background(Color.White, RoundedCornerShape(2.dp))
                    .padding(2.5.dp)
            )
            Text(
                text = "구글로 시작하기",
                modifier = Modifier
                    .weight(1f)
                    .padding(),
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun kakaoLogin(context: Context, viewModel: LoginViewModel) {
    viewModel.isLoading()
    val kakaoLoginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            viewModel.loginFail()
        } else if (token != null) {
            viewModel.firebaseLogin("kakao")
        }
    }

    UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
        if (error != null) {
            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                viewModel.loginFail()
                return@loginWithKakaoTalk
            }
            UserApiClient.instance.loginWithKakaoAccount(context, callback = kakaoLoginCallback)
        } else if (token != null) {
            Log.e(TAG, "카카오 로그인 성공 ${token.accessToken}")
            viewModel.firebaseLogin("kakao")
        }
    }
}

private fun naverLogin(context: Context, viewModel: LoginViewModel) {
    viewModel.isLoading()

    val naverLoginCallback = object : OAuthLoginCallback {
        override fun onError(errorCode: Int, message: String) {
            onFailure(errorCode, message)
        }

        override fun onFailure(httpStatus: Int, message: String) {
           viewModel.loginFail()
        }

        override fun onSuccess() {
            viewModel.firebaseLogin("naver")
        }
    }

    NaverIdLoginSDK.authenticate(context, naverLoginCallback)
}

private fun googleLogin(context: Context, coroutineScope: CoroutineScope, viewModel: LoginViewModel) {
    viewModel.isLoading()

    val credentialManager = CredentialManager.create(context)
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.Google_WebClient_Id)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    coroutineScope.launch {
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            viewModel.googleSignIn(result)
        } catch (e: GetCredentialException) {
            viewModel.loginFail()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onKakaoLogin = {}, onNaverLogin = {}, onGoogleLogin = {})
}