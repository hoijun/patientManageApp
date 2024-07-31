package com.example.patientManageApp.presentation.screen.main.userProfilePage

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.patientManageApp.presentation.AppScreen
import com.example.patientManageApp.presentation.DateBottomSheet
import com.example.patientManageApp.R
import com.example.patientManageApp.presentation.SubScreenHeader
import com.example.patientManageApp.presentation.innerShadow
import com.example.patientManageApp.presentation.moveScreen
import com.example.patientManageApp.presentation.noRippleClickable

@Composable
fun UserProfileScreen(navController: NavHostController) {
    val interactionSource = remember { MutableInteractionSource() }
    val backPressedState by remember { mutableStateOf(true) }
    var userName by remember { mutableStateOf("") }
    var isAbleEditUserName by remember { mutableStateOf(false) }
    var userBirth by remember { mutableStateOf("") }
    var isAbleEditUserBirth by remember { mutableStateOf(false) }
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    BackHandler(enabled = backPressedState) {
        moveScreen(navController, AppScreen.MyPage.route)
    }

    if (isBottomSheetOpen) {
        isAbleEditUserBirth = true
        DateBottomSheet(modifier = Modifier, closeSheet = {
            userBirth = it
            isBottomSheetOpen = false
        })
    }

    Column {
        SubScreenHeader(pageName = "개인 정보") {
            moveScreen(navController, AppScreen.MyPage.route)
        }

        Row(modifier = Modifier.padding(start = 20.dp, top = 20.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "이름", fontSize = 15.sp)
            OutlinedTextField(
                value = userName, onValueChange = {
                    userName = it
                }, modifier = Modifier
                    .padding(start = 15.dp)
                    .width(250.dp),
                enabled = isAbleEditUserName,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.DarkGray),
                singleLine = true
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.edit),
                contentDescription = "edit",
                modifier = Modifier
                    .noRippleClickable {
                        isAbleEditUserName = !isAbleEditUserName
                    }
                    .padding(end = 15.dp)
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

        Row(modifier = Modifier
            .padding(start = 20.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "생일",
                fontSize = 15.sp,
                modifier = Modifier.padding(end = 15.dp)
            )
            Box(
                modifier = Modifier
                    .border(
                        1.dp, if (isAbleEditUserBirth) Color.Gray else
                            Color.LightGray, RoundedCornerShape(4.dp)
                    )
                    .width(250.dp)
                    .height(55.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = userBirth,
                    modifier = Modifier.padding(start = 15.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.edit),
                contentDescription = "edit",
                modifier = Modifier
                    .noRippleClickable {
                        isBottomSheetOpen = true
                    }
                    .padding(end = 15.dp)
            )
        }

        AnimatedVisibility(visible = userBirth.isNotEmpty() or userName.isNotEmpty()) {
            Button(
                onClick = { },
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

        Surface(modifier = Modifier
            .fillMaxWidth()
            .height(15.dp)
            .innerShadow(
                RectangleShape,
                color = Color.Black.copy(0.3f),
                offsetY = (-2).dp,
                offsetX = (-2).dp
            )
            .innerShadow(
                RectangleShape,
                color = Color.Black.copy(0.3f),
                offsetY = 2.dp,
                offsetX = 2.dp
            ),
            color = Color(0xFFc0c2c4)
        ) {
        }

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
                Text(text = "카카오",
                    modifier = Modifier.padding(start = 10.dp))
            }
        }

        Surface(modifier = Modifier
            .fillMaxWidth()
            .height(15.dp)
            .innerShadow(
                RectangleShape,
                color = Color.Black.copy(0.3f),
                offsetY = (-2).dp,
                offsetX = (-2).dp
            )
            .innerShadow(
                RectangleShape,
                color = Color.Black.copy(0.3f),
                offsetY = 2.dp,
                offsetX = 2.dp
            ),
            color = Color(0xFFc0c2c4)
        ) {
        }

        Text(text = "로그아웃",
            modifier = Modifier
                .padding(vertical = 20.dp)
                .fillMaxWidth()
                .wrapContentWidth(),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp)

        Surface(modifier = Modifier
            .fillMaxWidth()
            .height(15.dp)
            .innerShadow(
                RectangleShape,
                color = Color.Black.copy(0.3f),
                offsetY = (-2).dp,
                offsetX = (-2).dp
            )
            .innerShadow(
                RectangleShape,
                color = Color.Black.copy(0.3f),
                offsetY = 2.dp,
                offsetX = 2.dp
            ),
            color = Color(0xFFc0c2c4)
        ) {
        }

        Text(text = "회원 탈퇴",
            modifier = Modifier
                .padding(top = 5.dp, end = 5.dp)
                .fillMaxWidth(),
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.End)
    }
}