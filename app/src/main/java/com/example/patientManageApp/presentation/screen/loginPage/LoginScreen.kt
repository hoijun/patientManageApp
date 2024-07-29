package com.example.patientManageApp.presentation.screen.loginPage

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.patientManageApp.BuildConfig
import com.example.patientManageApp.presentation.MainActivity
import com.example.patientManageApp.R
import com.example.patientManageApp.presentation.BackOnPressed
import com.example.patientManageApp.presentation.ScreenHeader
import com.example.patientManageApp.presentation.noRippleClickable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(), movePage: () -> Unit) {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val loginState: LoginUiState by viewModel.loginUiState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.googleLogin(activityResult = it)
    }

    BackOnPressed()

    when (loginState) {
        LoginUiState.SingIn -> {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        LoginUiState.LoginError -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(hostState = snackBarHostState)}
            ) {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(
                        message = "로그인에 실패했습니다.",
                        actionLabel = "닫기",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }

        LoginUiState.SingUp -> {
            movePage()
        }

        LoginUiState.IsLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.5f))
                    .zIndex(Float.MAX_VALUE)
                    .noRippleClickable(false) { },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black, strokeWidth = 6.dp, strokeCap = StrokeCap.Round)
            }
        }

        LoginUiState.IDlE ->  { }
    }

    Column {
        ScreenHeader(pageName = "로그인")
        Box(modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center) {
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

                Button(
                    onClick = { kakaoLogin(context, viewModel) },
                    colors = ButtonDefaults.buttonColors(Color(0xffFEE500)),
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
                            painter = painterResource(id = R.drawable.kakao_symbol),
                            contentDescription = "kakao",
                            modifier = Modifier.size(30.dp)
                        )
                        Text(
                            text = "카카오로 시작하기",
                            modifier = Modifier
                                .weight(1f)
                                .padding(),
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = { naverLogin(context, viewModel) },
                    colors = ButtonDefaults.buttonColors(Color(0xff03c75a)),
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 30.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.naver_symbol),
                            contentDescription = "naver",
                            modifier = Modifier.size(30.dp)
                        )
                        Text(
                            text = "네이버로 시작하기",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = { googleLogin(context, viewModel, launcher) },
                    colors = ButtonDefaults.buttonColors(Color(0xff4285F4)),
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
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
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
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

private fun googleLogin(
    context: Context,
    viewModel: LoginViewModel,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    viewModel.isLoading()
    val token = BuildConfig.Google_WebClient_Id
    val googleSignInOptions = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(token)
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
    launcher.launch(googleSignInClient.signInIntent)
}