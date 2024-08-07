package com.example.patientManageApp.presentation.screen.main.myPagePage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.patientManageApp.presentation.BackOnPressed
import com.example.patientManageApp.R
import com.example.patientManageApp.domain.entity.UserEntity
import com.example.patientManageApp.presentation.ScreenHeader
import com.example.patientManageApp.presentation.innerShadow
import com.example.patientManageApp.presentation.moveScreen
import com.example.patientManageApp.presentation.noRippleClickable
import com.example.patientManageApp.presentation.screen.main.MainViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun MyPageScreen(navController: NavHostController, viewModel: MainViewModel) {
    val userEntity = viewModel.userData.collectAsState()
    BackOnPressed()
    MyPageScreen(
        userEntity = userEntity.value,
        onUserInfoBtnClick = { moveScreen(navController, AppScreen.UserProfile.route) },
        onPatientInfoBtnClick = { moveScreen(navController, AppScreen.PatientProfile.route) }
    )
}

@Composable
private fun MyPageScreen(userEntity: UserEntity, onUserInfoBtnClick: () -> Unit, onPatientInfoBtnClick: () -> Unit) {
    val auth = Firebase.auth
    Column {
        ScreenHeader(pageName = "마이페이지")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(start = 20.dp, top = 25.dp, bottom = 25.dp)) {
                Row {
                    Text(
                        text = userEntity.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "님",
                        fontSize = 22.sp,
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .align(Alignment.Bottom)
                    )
                }
                Text(
                    text = auth.currentUser?.email ?: "",
                    fontSize = 17.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.arrow_forward),
                contentDescription = "자세히 보기",
                modifier = Modifier
                    .padding(end = 30.dp)
                    .noRippleClickable {
                        onUserInfoBtnClick()
                    }
            )
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

        SetMyPageScreenMenu("환자 정보 관리") {
            onPatientInfoBtnClick()
        }
        SetMyPageScreenMenu("문의 하기") { }
        SetMyPageScreenMenu("개인 정보 처리 방침") { }
        SetMyPageScreenMenu("앱 사용법 보기") { }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 22.5.dp, bottom = 22.5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "버전 정보",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                modifier = Modifier.padding(start = 20.dp)
            )

            Text(
                text = "version 0.0.1",
                fontSize = 15.sp,
                modifier = Modifier.padding(end = 20.dp)
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            drawLine(
                Color(0xFFc0c2c4),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}


@Composable
private fun SetMyPageScreenMenu(menuName: String, onClick: () -> Unit) {
    Text(
        text = menuName,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .padding(start = 20.dp, top = 22.5.dp, bottom = 22.5.dp)
            .fillMaxWidth()
            .noRippleClickable {
                onClick()
            }
    )
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        drawLine(
            Color(0xFFc0c2c4),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 1.dp.toPx()
        )
    }
}