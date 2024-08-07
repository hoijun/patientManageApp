package com.example.patientManageApp.presentation.screen.main.patientProfilePage

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.Divider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.patientManageApp.presentation.AppScreen
import com.example.patientManageApp.presentation.DateBottomSheet
import com.example.patientManageApp.R
import com.example.patientManageApp.domain.entity.PatientEntity
import com.example.patientManageApp.presentation.CustomDivider
import com.example.patientManageApp.presentation.LoadingDialog
import com.example.patientManageApp.presentation.SubScreenHeader
import com.example.patientManageApp.presentation.moveScreen
import com.example.patientManageApp.presentation.noRippleClickable
import com.example.patientManageApp.presentation.screen.main.MainViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PatientProfileScreen(navController: NavHostController, mainViewModel: MainViewModel, patientProfileViewModel: PatientProfileViewModel = hiltViewModel()) {
    val backPressedState by remember { mutableStateOf(true) }
    val patientEntity by mainViewModel.patientData.collectAsState()
    val patientProfileUiState by patientProfileViewModel.patientProfileUiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    BackHandler(enabled = backPressedState) {
        moveScreen(navController, AppScreen.MyPage.route)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) {
        when (patientProfileUiState) {
            PatientProfileUiState.Idle -> { }
            PatientProfileUiState.Loading -> {
                LoadingDialog()
            }

            PatientProfileUiState.Success -> {
                LaunchedEffect(snackBarHostState) {
                    snackBarHostState.showSnackbar(
                        message = "정보를 업데이트 했습니다.",
                        actionLabel = "닫기",
                        duration = SnackbarDuration.Short
                    )

                    patientProfileViewModel.isIdle()
                }
            }

            PatientProfileUiState.Error -> {
                LaunchedEffect(snackBarHostState) {
                    snackBarHostState.showSnackbar(
                        message = "오류가 발생 했습니다.",
                        actionLabel = "닫기",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }

        PatientProfileScreen(
            patientEntity,
            onBackButtonClick = { moveScreen(navController, AppScreen.MyPage.route) },
            onSaveButtonClick = {
                mainViewModel.updatePatientData(it)
                patientProfileViewModel.updatePatientData(it)
            }
        )
    }
}

@Composable
fun PatientProfileScreen(patientEntity: PatientEntity, onBackButtonClick: () -> Unit, onSaveButtonClick: (patientEntity: PatientEntity) -> Unit) {
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    var patientName by remember { mutableStateOf(patientEntity.name) }
    var patientBirth by remember { mutableStateOf(patientEntity.birth) }
    var isAbleEditPatientName by remember { mutableStateOf(false) }
    var isAbleEditPatientBirth by remember { mutableStateOf(false) }
    val btnVisibility by remember {
        derivedStateOf { (isAbleEditPatientName || isAbleEditPatientBirth) }
    }

    if (isBottomSheetOpen) {
        isAbleEditPatientBirth = true
        DateBottomSheet(modifier = Modifier, closeSheet = {
            patientBirth = it
            isBottomSheetOpen = false
        })
    }

    Column {
        SubScreenHeader(pageName = "환자 정보") {
            onBackButtonClick()
        }

        NameField(
            value = patientName,
            isAbleEditPatientName = isAbleEditPatientName,
            onValueChange = { patientName = it },
            onEditButtonClick = { isAbleEditPatientName = it })

        CustomDivider(vertical = 20.dp, horizontal = 20.dp)

        BirthField(value = patientBirth,
            isAbleEditPatientBirth = isAbleEditPatientBirth,
            onEditButtonClick = { isBottomSheetOpen = !isBottomSheetOpen }
        )

        Spacer(modifier = Modifier.weight(1f))

        SaveButton(visible = btnVisibility) {
            onSaveButtonClick(PatientEntity(patientName, patientBirth))
            isAbleEditPatientName = false
            isAbleEditPatientBirth = false
        }
    }
}

@Composable
private fun NameField(
    value: String,
    isAbleEditPatientName: Boolean,
    onValueChange: (String) -> Unit,
    onEditButtonClick: (isAbleEditPatientName: Boolean) -> Unit
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
            enabled = isAbleEditPatientName,
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
                    onEditButtonClick(!isAbleEditPatientName)
                }
                .padding(end = 15.dp)
        )
    }
}

@Composable
private fun BirthField(
    value: String,
    isAbleEditPatientBirth: Boolean,
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
                    1.dp, if (isAbleEditPatientBirth) Color.DarkGray else {
                        Color.LightGray
                    }, RoundedCornerShape(4.dp)
                )
                .width(250.dp)
                .height(55.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = value,
                color = if (isAbleEditPatientBirth) Color.DarkGray else {
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

@Preview(showBackground = true)
@Composable
fun PatientProfileScreenPreview() {
    PatientProfileScreen(PatientEntity("aaa", "aaaa"), {}, {})
}