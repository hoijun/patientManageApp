package com.example.patientManageApp.presenter.screen.settingUserProfilePage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.patientManageApp.presenter.DateBottomSheet
import com.example.patientManageApp.presenter.ScreenHeader
import com.example.patientManageApp.presenter.noRippleClickable

@Composable
fun SettingUserProfile(movePage: () -> Unit) {
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    var userBirth by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }

    BackHandler { }

    if (isBottomSheetOpen) {
        DateBottomSheet(modifier = Modifier, closeSheet = {
            userBirth = it
            isBottomSheetOpen = false
        })
    }

    Column {
        ScreenHeader(pageName = "개인 정보 입력")
        Row(
            modifier = Modifier.padding(start = 20.dp, top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "이름", fontSize = 15.sp)
            OutlinedTextField(
                value = userName,
                onValueChange = {
                    userName = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.DarkGray,
                    focusedBorderColor = Color.DarkGray),
                singleLine = true
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            drawLine(
                Color(0xFFc0c2c4),
                start = Offset(50f, 0f),
                end = Offset(size.width - 50f, 0f),
                strokeWidth = 1.dp.toPx()
            )
        }

        Row(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "생일",
                fontSize = 15.sp,
                modifier = Modifier.padding(end = 15.dp)
            )
            Box(
                modifier = Modifier
                    .border(1.dp, Color.DarkGray, RoundedCornerShape(4.dp))
                    .fillMaxWidth()
                    .height(55.dp)
                    .noRippleClickable {
                        isBottomSheetOpen = true
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = userBirth,
                    modifier = Modifier.padding(start = 15.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { movePage() },
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
            Text("다음 으로")
        }
    }
}