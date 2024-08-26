package com.example.patientManageApp.presentation.screen.main.userProfilePage

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.patientManageApp.R
import com.example.patientManageApp.domain.entity.UserEntity
import com.example.patientManageApp.presentation.AppScreen
import com.example.patientManageApp.presentation.CustomDivider
import com.example.patientManageApp.presentation.DateBottomSheet
import com.example.patientManageApp.presentation.LoadingDialog
import com.example.patientManageApp.presentation.LoginActivity
import com.example.patientManageApp.presentation.ShadowDivider
import com.example.patientManageApp.presentation.SubScreenHeader
import com.example.patientManageApp.presentation.WithdrawalWarningDialog
import com.example.patientManageApp.presentation.moveScreen
import com.example.patientManageApp.presentation.noRippleClickable
import com.example.patientManageApp.presentation.screen.main.MainViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserProfileScreen(navController: NavHostController, mainViewModel: MainViewModel, userProfileViewModel: UserProfileViewModel = hiltViewModel()) {
    val backPressedState by remember { mutableStateOf(true) }
    val userProfileUiState by userProfileViewModel.userProfileUiState.collectAsState()
    val userEntity by mainViewModel.userData.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    BackHandler(enabled = backPressedState) {
        snackBarHostState.currentSnackbarData?.dismiss()
        moveScreen(navController, AppScreen.MyPage.route)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) {
        when (userProfileUiState) {
            UserProfileUiState.Idle -> { }

            UserProfileUiState.Loading -> {
                LoadingDialog()
            }

            UserProfileUiState.UpdateSuccess -> {
                LaunchedEffect(snackBarHostState) {
                    snackBarHostState.showSnackbar(
                        message = "정보를 업데이트 했습니다.",
                        actionLabel = "닫기",
                        duration = SnackbarDuration.Short
                    )
                    userProfileViewModel.isIdle()
                }
            }

            UserProfileUiState.LogoutSuccess -> {
                val intent = Intent(context, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("ShowSnackBar", true)
                    putExtra("SnackBarMessage", "로그아웃 되었습니다.")
                }
                context.startActivity(intent)
            }

            UserProfileUiState.WithdrawalSuccess -> {
                val intent = Intent(context, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("ShowSnackBar", true)
                    putExtra("SnackBarMessage", "탈퇴 되었습니다.")
                }
                context.startActivity(intent)
            }

            UserProfileUiState.Error -> {
                LaunchedEffect(snackBarHostState) {
                    snackBarHostState.showSnackbar(
                        message = "오류가 발생 했습니다.",
                        actionLabel = "닫기",
                        duration = SnackbarDuration.Short
                    )
                    userProfileViewModel.isIdle()
                }
            }
        }

        UserProfileScreen(userEntity = userEntity,
            email = Firebase.auth.currentUser?.email ?: "",
            onBackButtonClick = {
                snackBarHostState.currentSnackbarData?.dismiss()
                moveScreen(navController, AppScreen.MyPage.route)
            },
            onSaveButtonClick = {
                mainViewModel.updateUserData(it)
                userProfileViewModel.updateUserData(it)
            },
            onLogoutButtonClick = { userProfileViewModel.logout() },
            onWithdrawalButtonClick = { userProfileViewModel.withdrawal() }
        )
    }
}

@Composable
private fun UserProfileScreen(
    userEntity: UserEntity,
    email: String, onBackButtonClick: () -> Unit,
    onSaveButtonClick: (userEntity: UserEntity) -> Unit,
    onLogoutButtonClick: () -> Unit,
    onWithdrawalButtonClick: () -> Unit
) {
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    var isDialogOpen by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf(userEntity.name) }
    var userBirth by remember { mutableStateOf(userEntity.birth) }
    var isAbleEditUserName by remember { mutableStateOf(false) }
    var isAbleEditUserBirth by remember { mutableStateOf(false) }
    val btnVisibility by remember {
        derivedStateOf {
            (isAbleEditUserName || isAbleEditUserBirth) &&
                    (userEntity.name != userName || userEntity.birth != userBirth) && userName != ""
        }
    }

    if (isBottomSheetOpen) {
        isAbleEditUserBirth = true
        DateBottomSheet(modifier = Modifier, closeSheet = {
            userBirth = it
            isBottomSheetOpen = false
        })
    }

    if(isDialogOpen) {
        WithdrawalWarningDialog(
            title = "정말로 탈퇴하시겠습니까?",
            description = "절대로 복구 불가능 합니다.\n밑의 문자를 똑같이 입력해주세요.",
            onDismissRequest = { isDialogOpen = false },
            onClickConfirm = {
                onWithdrawalButtonClick()
                isDialogOpen = false
            }
        )
    }

    Column {
        SubScreenHeader(pageName = "개인 정보") {
            onBackButtonClick()
        }

        NameField(
            value = userName,
            isAbleEditUserName = isAbleEditUserName,
            onValueChange = { userName = it },
            onEditButtonClick = { isAbleEditUserName = it }
        )

        CustomDivider(vertical = 20.dp, horizontal = 20.dp)

        BirthField(value = userBirth,
            isAbleEditUserBirth = isAbleEditUserBirth,
            onEditButtonClick = { isBottomSheetOpen = !isBottomSheetOpen }
        )

        SaveButton(visible = btnVisibility) {
            onSaveButtonClick(UserEntity(userName, userBirth))
            isAbleEditUserName = false
            isAbleEditUserBirth = false
        }

        ShadowDivider()

        EmailField(email = email)

        ShadowDivider()
        LogoutButton { onLogoutButtonClick() }
        ShadowDivider()
        WithdrawalButton { isDialogOpen = true }
    }
}

@Composable
private fun NameField(
    value: String,
    isAbleEditUserName: Boolean,
    onValueChange: (String) -> Unit,
    onEditButtonClick: (isAbleEditUserName: Boolean) -> Unit
) {
    Row(modifier = Modifier.padding(start = 20.dp, top = 20.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = "이름", fontSize = 15.sp)
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            modifier = Modifier
                .padding(start = 15.dp)
                .width(250.dp),
            enabled = isAbleEditUserName,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.DarkGray,
                unfocusedBorderColor = Color.DarkGray,
                disabledBorderColor = Color.LightGray,
                disabledTextColor = Color.LightGray),
            singleLine = true,
        )

        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.edit),
            contentDescription = "edit",
            modifier = Modifier
                .noRippleClickable {
                    onEditButtonClick(!isAbleEditUserName)
                }
                .padding(end = 15.dp)
        )
    }
}

@Composable
private fun BirthField(
    value: String,
    isAbleEditUserBirth: Boolean,
    onEditButtonClick: () -> Unit
) {
    Row(modifier = Modifier.padding(start = 20.dp, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "생일",
            fontSize = 15.sp,
            modifier = Modifier.padding(end = 15.dp)
        )
        Box(
            modifier = Modifier
                .border(
                    1.dp, if (isAbleEditUserBirth) Color.DarkGray else {
                        Color.LightGray
                    }, RoundedCornerShape(4.dp)
                )
                .width(250.dp)
                .height(55.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = value,
                color = if (isAbleEditUserBirth) Color.DarkGray else {
                    Color.LightGray
                },
                modifier = Modifier.padding(start = 15.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.edit),
            contentDescription = "edit",
            modifier = Modifier
                .noRippleClickable {
                    onEditButtonClick()
                }
                .padding(end = 15.dp)
        )
    }
}

@Composable
private fun SaveButton(visible: Boolean, onSaveButtonClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    AnimatedVisibility(visible = visible) {
        Button(
            onClick = {
                onSaveButtonClick()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFc0c2c4),
                contentColor = Color.Black
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
            Text("저장 하기")
        }
    }
}

@Composable
private fun EmailField(email: String) {
    Row(modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = "이메일",
            fontSize = 15.sp,
            modifier = Modifier.padding(end = 15.dp))
        Box(modifier = Modifier
            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
            .width(235.dp)
            .height(40.dp),
            contentAlignment = Alignment.CenterStart) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Image(
                    painter = if (email.contains("kakao.com"))
                        painterResource(id = R.drawable.kakaoemail_symbol)
                    else if (email.contains("naver.com"))
                        painterResource(id = R.drawable.naveremail_symbol)
                    else if (email.contains("gmail.com"))
                        painterResource(id = R.drawable.google_symbol)
                    else painterResource(id = R.drawable.warning),
                    contentDescription = "emailIcon",
                    modifier = Modifier.size(20.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = email,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Text(text = "로그아웃",
        modifier = Modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth()
            .wrapContentWidth()
            .noRippleClickable { onClick() },
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp)
}

@Composable
private fun WithdrawalButton(onClick: () -> Unit) {
    Text(text = "회원 탈퇴",
        modifier = Modifier
            .padding(top = 5.dp, end = 5.dp)
            .fillMaxWidth()
            .noRippleClickable { onClick() },
        fontSize = 12.sp,
        color = Color.Gray,
        textAlign = TextAlign.End)
}

@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    UserProfileScreen(
        userEntity = UserEntity("홍길동", ""),
        email = "",
        onBackButtonClick = {},
        onSaveButtonClick = {},
        onLogoutButtonClick = {},
        onWithdrawalButtonClick = {}
    )
}