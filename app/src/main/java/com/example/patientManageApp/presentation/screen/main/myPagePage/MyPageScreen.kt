package com.example.patientManageApp.presentation.screen.main.myPagePage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.patientManageApp.R
import com.example.patientManageApp.domain.entity.UserEntity
import com.example.patientManageApp.presentation.AppScreen
import com.example.patientManageApp.presentation.BackOnPressed
import com.example.patientManageApp.presentation.CustomDivider
import com.example.patientManageApp.presentation.ScreenHeader
import com.example.patientManageApp.presentation.ShadowDivider
import com.example.patientManageApp.presentation.moveScreen
import com.example.patientManageApp.presentation.noRippleClickable
import com.example.patientManageApp.presentation.screen.main.MainViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun MyPageScreen(navController: NavHostController, viewModel: MainViewModel) {
    val userEntity = viewModel.userData.collectAsState()
    val email = Firebase.auth.currentUser?.email ?: ""
    BackOnPressed()
    MyPageScreen(
        email = email,
        userEntity = userEntity.value,
        onUserInfoBtnClick = { moveScreen(navController, AppScreen.UserProfile.route) },
        onPatientInfoBtnClick = { moveScreen(navController, AppScreen.PatientProfile.route) }
    )
}

@Composable
private fun MyPageScreen(
    email: String,
    userEntity: UserEntity,
    onUserInfoBtnClick: () -> Unit,
    onPatientInfoBtnClick: () -> Unit,
) {
    Column {
        ScreenHeader(pageName = "마이페이지")
        UserProfileField(userEntity, email) { onUserInfoBtnClick() }
        ShadowDivider()

        SetMyPageScreenMenu("환자 정보 관리") { onPatientInfoBtnClick() }
        SetMyPageScreenMenu("문의 하기") { }
        SetMyPageScreenMenu("개인 정보 처리 방침") { }
        SetMyPageScreenMenu("앱 사용법 보기") { }
        VersionField("1.0.0")
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
    CustomDivider(horizontal = 0.dp, vertical = 0.dp)
}

@Composable
private fun UserProfileField(userEntity: UserEntity, email: String, onUserInfoBtnClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, top = 25.dp, bottom = 25.dp)
            .weight(1f)
        ) {
            Row {
                Text(
                    text = userEntity.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
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
                text = email,
                fontSize = 17.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 10.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
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
}

@Composable
private fun VersionField(version: String) {
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
            text = "version $version",
            fontSize = 15.sp,
            modifier = Modifier.padding(end = 20.dp)
        )
    }
    CustomDivider(horizontal = 0.dp, vertical = 0.dp)
}

@Preview(showBackground = true)
@Composable
fun MyPage() {
    MyPageScreen(
        email = "wjd3063@google.com",
        userEntity = UserEntity("홍길동", ""),
        onUserInfoBtnClick = {},
        onPatientInfoBtnClick = {},
    )
}