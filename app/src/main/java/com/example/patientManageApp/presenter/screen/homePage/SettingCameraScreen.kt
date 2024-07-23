package com.example.patientManageApp.presenter.screen.homePage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.patientManageApp.presenter.AppScreen
import com.example.patientManageApp.presenter.SubScreenHeader
import com.example.patientManageApp.presenter.moveScreen

@Composable
fun SettingCameraScreen(navController: NavHostController) {
    var cameraName by remember { mutableStateOf("") }
    var rtspAddress by remember { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }
    val backPressedState by remember { mutableStateOf(true) }

    BackHandler(enabled = backPressedState) {
        moveScreen(navController, AppScreen.Home.route)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .imePadding()) {
        SubScreenHeader(pageName = "카메라 설정") {
            moveScreen(navController, AppScreen.Home.route)
        }
        OutlinedTextField(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 30.dp, end = 30.dp),
            value = cameraName,
            onValueChange = {
                cameraName = it
            }, label = {
                Text(text = "카메라 이름", color = Color.Gray)
            },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.DarkGray),
            singleLine = true)

        OutlinedTextField(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 15.dp),
            value = rtspAddress,
            onValueChange = {
                rtspAddress = it
            }, label = {
                Text(text = "카메라 RTSP 주소", color = Color.Gray)
            },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.DarkGray),
            singleLine = true)

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFc0c2c4),
                contentColor = Color.Black
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
}