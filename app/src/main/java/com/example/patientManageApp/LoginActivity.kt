package com.example.patientManageApp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.patientManageApp.Constant.Companion.BackOnPressed
import com.example.patientManageApp.Constant.Companion.DateBottomSheet
import com.example.patientManageApp.Constant.Companion.ScreenHeader
import com.example.patientManageApp.Constant.Companion.moveScreen
import com.example.patientManageApp.Constant.Companion.noRippleClickable
import com.example.patientManageApp.ui.theme.PatientManageAppTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PatientManageAppTheme {
                Login()
            }
        }
    }
}

@Composable
fun Login() {
    val navController = rememberNavController()
    LoginNavHost(navController = navController, startDestination = LoginAppScreen.Login.route)
}

@Composable
private fun LoginNavHost(navController: NavHostController, startDestination: String) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            val currentIndex = initialState.destination.route?.let { getIndexForRoute(it)} ?: -1
            val targetIndex = targetState.destination.route?.let { getIndexForRoute(it) } ?: -1

            if (currentIndex < targetIndex) {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(100))
            } else {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(100))
            }
        },
        exitTransition = {
            val currentIndex = initialState.destination.route?.let { getIndexForRoute(it) } ?: -1
            val targetIndex = targetState.destination.route?.let { getIndexForRoute(it) } ?: -1

            if (targetIndex > currentIndex) {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(100))
            } else {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(100))
            }
        }
    ) {
        composable(route = LoginAppScreen.Login.route) {
            LoginScreen(navController)
        }

        composable(route = LoginAppScreen.UserProfile.route) {
            SettingUserProfile(navController)
        }

        composable(route = LoginAppScreen.PatientProfile.route) {
           SettingPatientProfile()
        }
    }
}

private fun getIndexForRoute(route: String): Int {
    return when (route) {
        LoginAppScreen.Login.route -> 0
        LoginAppScreen.UserProfile.route -> 1
        LoginAppScreen.PatientProfile.route -> 2
        else -> -1
    }
}

@Composable
private fun LoginScreen(navController: NavHostController) {
    BackOnPressed()
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
                    onClick = { kakaoLogin(navController) },
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
                    onClick = { naverLogin(navController) },
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
                    onClick = { googleLogin(navController) },
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

private fun kakaoLogin(navController: NavHostController) {
    moveScreen(navController, LoginAppScreen.UserProfile.route)
}

private fun naverLogin(navController: NavHostController) {
    moveScreen(navController, LoginAppScreen.UserProfile.route)
}

private fun googleLogin(navController: NavHostController) {
    moveScreen(navController, LoginAppScreen.UserProfile.route)
}

@Composable
private fun SettingUserProfile(navController: NavHostController) {
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
            onClick = { moveScreen(navController, LoginAppScreen.PatientProfile.route) },
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

@Composable
private fun SettingPatientProfile() {
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    var patientName by remember { mutableStateOf("") }
    var patientBirth by remember { mutableStateOf("") }
    val context = LocalContext.current

    BackHandler { }

    if (isBottomSheetOpen) {
        DateBottomSheet(modifier = Modifier, closeSheet = {
            patientBirth = it
            isBottomSheetOpen = false
        })
    }

    Column {
        ScreenHeader(pageName = "환자 정보 입력")

        Row(modifier = Modifier.padding(start = 20.dp, top = 20.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "이름", fontSize = 15.sp)
            OutlinedTextField(
                value = patientName,
                onValueChange = {
                    patientName = it
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

        Row(modifier = Modifier.padding(start = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically) {
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
                    text = patientBirth,
                    modifier = Modifier.padding(start = 15.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
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
            Text("완료")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PatientManageAppTheme {
        Login()
    }
}