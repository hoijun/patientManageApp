package com.example.patientManageApp.presentation.screen.main.settingCameraPage

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.patientManageApp.domain.entity.CameraEntity
import com.example.patientManageApp.presentation.AppScreen
import com.example.patientManageApp.presentation.CustomDivider
import com.example.patientManageApp.presentation.LoadingDialog
import com.example.patientManageApp.presentation.WarningDialog
import com.example.patientManageApp.presentation.moveScreen
import com.example.patientManageApp.presentation.noRippleClickable
import com.example.patientManageApp.presentation.screen.main.MainViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingCameraScreen(navController: NavHostController, cameraEntity: CameraEntity, mainViewModel: MainViewModel, settingCameraViewModel: SettingCameraViewModel = hiltViewModel()) {
    val settingCameraUiState by settingCameraViewModel.settingCameraUiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val backPressedState by remember { mutableStateOf(true) }
    var showInvalidRtspSnackBar by remember { mutableStateOf(false) }
    var snackBarMessage by remember { mutableStateOf("") }
    val cameraList by mainViewModel.cameraData.collectAsState()
    val cameraIndex = cameraList.indexOf(cameraEntity)

    BackHandler(enabled = backPressedState) {
        snackBarHostState.currentSnackbarData?.dismiss()
        moveScreen(navController, AppScreen.Home.route)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) {
        when (settingCameraUiState) {
            SettingCameraUiState.Idle -> {}
            SettingCameraUiState.Loading -> {
                LoadingDialog()
            }

            SettingCameraUiState.Success -> {
                settingCameraViewModel.isIdle()
                moveScreen(navController, AppScreen.Home.route)
            }

            SettingCameraUiState.Error -> {
                LaunchedEffect(snackBarHostState) {
                    snackBarHostState.showSnackbar(
                        message = "오류가 발생 했습니다.",
                        actionLabel = "닫기",
                        duration = SnackbarDuration.Short
                    )
                    settingCameraViewModel.isIdle()
                }
            }
        }

        if (showInvalidRtspSnackBar) {
            LaunchedEffect(snackBarHostState) {
                snackBarHostState.showSnackbar(
                    message = snackBarMessage,
                    actionLabel = "닫기",
                    duration = SnackbarDuration.Short
                )
                showInvalidRtspSnackBar = false
            }
        }

        SettingCameraScreen(
            cameraEntity = cameraEntity,
            cameraIndex = cameraIndex,
            onBackButtonClick = {
                snackBarHostState.currentSnackbarData?.dismiss()
                moveScreen(navController, AppScreen.Home.route)
            },
            onRemoveBtn = {
                mainViewModel.updateCameraData(cameraList.filterIndexed { index, _ -> index != cameraIndex })
                settingCameraViewModel.updateCameraData(cameraList.filterIndexed { index, _ -> index != cameraIndex })
            },
            onSaveButtonClick = OnSaveButtonClick@{
                if (!isValidRtspAddress(it.rtspAddress)) {
                    showInvalidRtspSnackBar = true
                    snackBarMessage = "올바른 RTSP 주소가 아닙니다."
                    return@OnSaveButtonClick
                }

                if (checkDuplicateCamera(cameraList, it, cameraIndex)) {
                    showInvalidRtspSnackBar = true
                    snackBarMessage = "중복된 내용이 존재 합니다."
                    return@OnSaveButtonClick
                }

                val newCameraEntities = cameraList.toMutableList()
                if (cameraIndex == -1) {
                    newCameraEntities.add(it)
                } else {
                    newCameraEntities[cameraIndex] = it
                }
                mainViewModel.updateCameraData(newCameraEntities.toList())
                settingCameraViewModel.updateCameraData(newCameraEntities.toList())
            }
        )
    }
}

@Composable
private fun SettingCameraScreen(
    cameraEntity: CameraEntity,
    cameraIndex: Int,
    onBackButtonClick: () -> Unit,
    onRemoveBtn: () -> Unit,
    onSaveButtonClick: (cameraEntity: CameraEntity) -> Unit = {}
) {
    var cameraName by remember { mutableStateOf(cameraEntity.name) }
    var rtspAddress by remember { mutableStateOf(cameraEntity.rtspAddress) }
    var isDialogOpen by remember { mutableStateOf(false) }
    val isSaveButtonEnabled by remember {
        derivedStateOf {
            if (cameraIndex == -1) {
                cameraEntity.name != cameraName && cameraEntity.rtspAddress != rtspAddress
            } else {
                ( cameraEntity.name != cameraName || cameraEntity.rtspAddress != rtspAddress ) && ( cameraName != "" && rtspAddress != "")
            }
        }
    }

    if(isDialogOpen) {
        WarningDialog(
            title = "정말로 삭제하시겠습니까?",
            description = "삭제한 후에는 복구가 불가능합니다.",
            onDismissRequest = { isDialogOpen = false },
            onClickConfirm = {
                onRemoveBtn()
                isDialogOpen = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SettingScreenHeader(
            pageName = "카메라 설정",
            cameraIndex = cameraIndex,
            onBackBtnClick = { onBackButtonClick() },
            onRemoveBtn = {
                isDialogOpen = true
            }
        )
        CameraNameField(cameraName = cameraName) {
            cameraName = it
        }

        RtspAddressField(rtspAddress = rtspAddress) {
            rtspAddress = it
        }

        Spacer(modifier = Modifier.weight(1f))

        SaveButton(isSaveButtonEnabled) { onSaveButtonClick(CameraEntity(cameraName, rtspAddress)) }
    }
}

@Composable
fun SettingScreenHeader(
    pageName: String,
    cameraIndex: Int,
    onBackBtnClick: () -> Unit,
    onRemoveBtn: () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "arrow_back",
                modifier = Modifier
                    .padding(start = 20.dp)
                    .noRippleClickable {
                        onBackBtnClick()
                    })
            Text(
                text = pageName,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(vertical = 20.dp, horizontal = 5.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            if (cameraIndex != -1) {
                Text(
                    "삭제하기",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .noRippleClickable { onRemoveBtn() }
                )
            }
        }
        CustomDivider(horizontal = 0.dp, vertical = 0.dp)
    }
}

@Composable
private fun CameraNameField(cameraName: String, onCameraNameChange: (String) -> Unit) {
    Row(
        modifier = Modifier.padding(start = 20.dp, top = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "카메라 이름", fontSize = 15.sp, modifier = Modifier.width(80.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp),
            value = cameraName,
            onValueChange = {
                onCameraNameChange(it)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.DarkGray,
                unfocusedBorderColor = Color.DarkGray),
            singleLine = true,
        )
    }
}

@Composable
private fun RtspAddressField(rtspAddress: String, onRtspAddressChange: (String) -> Unit) {
    Row(modifier = Modifier.padding(start = 20.dp, top = 20.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = "RTSP 주소", fontSize = 15.sp, modifier = Modifier.width(80.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 15.dp),
            value = rtspAddress,
            onValueChange = {
                onRtspAddressChange(it)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.DarkGray,
                unfocusedBorderColor = Color.DarkGray),
            singleLine = true
        )
    }
}

@Composable
private fun SaveButton(enabled: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Button(onClick = { onClick() },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFc0c2c4),
            contentColor = Color.Black,
            disabledContainerColor = Color(0xFFc0c2c4).copy(alpha = 0.5f),
            disabledContentColor = Color.Black.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .padding(horizontal = 30.dp, vertical = 30.dp)
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

private fun isValidRtspAddress(address: String): Boolean {
    return address.startsWith("rtsp://")
}

private fun checkDuplicateCamera(
    cameraList: List<CameraEntity>,
    cameraEntity: CameraEntity,
    cameraIndex: Int
): Boolean {
    if (cameraIndex == -1)
        return cameraList.any { it.name == cameraEntity.name || it.rtspAddress == cameraEntity.rtspAddress }
    return cameraList.any {
        it != cameraList[cameraIndex] && (it.name == cameraEntity.name || it.rtspAddress == cameraEntity.rtspAddress) }
}

@Preview(showBackground = true)
@Composable
fun SettingCameraScreenPreview() {
    SettingCameraScreen(
        cameraEntity = CameraEntity("테스트", "테스트"),
        cameraIndex = 0,
        onBackButtonClick = {},
        onRemoveBtn = {}
    )
}