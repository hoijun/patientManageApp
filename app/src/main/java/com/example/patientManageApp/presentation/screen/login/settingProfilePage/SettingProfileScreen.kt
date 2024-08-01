package com.example.patientManageApp.presentation.screen.login.settingProfilePage

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.patientManageApp.BuildConfig
import com.example.patientManageApp.presentation.DateBottomSheet
import com.example.patientManageApp.presentation.LoadingDialog
import com.example.patientManageApp.presentation.MainActivity
import com.example.patientManageApp.presentation.ScreenHeader
import com.example.patientManageApp.presentation.noRippleClickable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun SettingProfileProfile(loginSns: String?, viewModel: SettingProfileViewModel = hiltViewModel()) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val settingUserUiState: SettingProfileUiState by viewModel.settingUserUiState.collectAsState()

    BackHandler { }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) {
        when (settingUserUiState) {
            SettingProfileUiState.Error -> {
                LaunchedEffect(snackBarHostState) {
                    snackBarHostState.showSnackbar(
                        message = "오류가 발생 했습니다.",
                        actionLabel = "닫기",
                        duration = SnackbarDuration.Short
                    )
                }

            }

            SettingProfileUiState.Loading -> {
                LoadingDialog()
            }

            SettingProfileUiState.Success -> {
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }

            SettingProfileUiState.Idle -> {}
        }

        SettingProfileScreen { userName, userBirth, patientName, patientBirth ->
            if (loginSns == null) {
                return@SettingProfileScreen
            }
            if (loginSns == "google") {
                val token = BuildConfig.Google_WebClient_Id
                val googleSignInOptions = GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(token)
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
                viewModel.googleSignUp(
                    userName,
                    userBirth,
                    patientName,
                    patientBirth,
                    googleSignInClient
                )
            } else {
                viewModel.singUp(loginSns, userName, userBirth, patientName, patientBirth)
            }
        }
    }
}

@Composable
private fun SettingProfileScreen(
    onClick: (userBirth: String,
         userName: String,
         patientName: String,
         patientBirth: String) -> Unit)
{
    var userBirth by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var patientName by remember { mutableStateOf("") }
    var patientBirth by remember { mutableStateOf("") }

    val isSubmitEnabled by remember {
        derivedStateOf {
            userName.isNotEmpty() && userBirth.isNotEmpty()
                    && patientName.isNotEmpty() && patientBirth.isNotEmpty()
        }
    }

    var isUserBirthBottomSheetOpen by remember { mutableStateOf(false) }
    var isPatientBirthBottomSheetOpen by remember { mutableStateOf(false) }

    if (isUserBirthBottomSheetOpen) {
        DateBottomSheet(modifier = Modifier, closeSheet = {
            userBirth = it
            isUserBirthBottomSheetOpen = false
        })
    }

    if (isPatientBirthBottomSheetOpen) {
        DateBottomSheet(modifier = Modifier, closeSheet = {
            patientBirth = it
            isPatientBirthBottomSheetOpen = false
        })
    }

    Column {
        ScreenHeader(pageName = "필수 정보 입력")

        NameInputField(description = "사용자 이름", value = userName) { userName = it }

        Divider(horizontal = 20.dp, vertical = 20.dp)

        BirthInputField(description = "사용자 생일", value = userBirth) { isUserBirthBottomSheetOpen = true }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            thickness = 1.dp,
            color = Color(0xFFc0c2c4)
        )

        NameInputField(description = "환자 이름 ", value = patientName) { patientName = it }

        Divider(horizontal = 20.dp, vertical = 20.dp)

        BirthInputField(description = "환자 생일", value = patientBirth) { isPatientBirthBottomSheetOpen = true }

        Spacer(modifier = Modifier.weight(1f))

        SubmitButton(enabled = isSubmitEnabled) {
            onClick(userBirth, userName, patientName, patientBirth)
        }
    }
}

@Composable
private fun NameInputField(description: String, value: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier.padding(start = 20.dp, top = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = description, fontSize = 15.sp, modifier = Modifier.width(100.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.DarkGray,
                focusedBorderColor = Color.DarkGray),
            singleLine = true
        )
    }
}

@Composable
private fun BirthInputField(description: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = description,
            fontSize = 15.sp,
            modifier = Modifier.padding(end = 15.dp).width(100.dp)
        )
        Box(
            modifier = Modifier
                .border(1.dp, Color.DarkGray, RoundedCornerShape(4.dp))
                .fillMaxWidth()
                .height(55.dp)
                .noRippleClickable {
                    onClick()
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = value,
                modifier = Modifier.padding(start = 15.dp)
            )
        }
    }
}

@Composable
private fun Divider(horizontal: Dp, vertical: Dp) {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontal, vertical = vertical),
        thickness = 1.dp,
        color = Color(0xFFc0c2c4)
    )
}

@Composable
private fun SubmitButton(enabled: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Button(
        onClick = { onClick() },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFc0c2c4),
            contentColor = Color.Black,
            disabledContainerColor = Color(0xFFc0c2c4).copy(alpha = 0.5f),
            disabledContentColor = Color.Black.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .padding(start = 30.dp, end = 30.dp, bottom = 20.dp)
            .indication(
                interactionSource = interactionSource,
                rememberRipple(color = Color(0xfff1f3f5))
            )
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        interactionSource = interactionSource
    ) {
        Text("다음으로")
    }
}

@Preview(showBackground = true)
@Composable
fun SettingUserProfileScreenPreview() {
    SettingProfileScreen { _, _ , _, _ -> }
}