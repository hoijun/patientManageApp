package com.example.patientManageApp.presentation.screen.main.homePage

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.patientManageApp.presentation.AppScreen
import com.example.patientManageApp.presentation.BackOnPressed
import com.example.patientManageApp.R
import com.example.patientManageApp.presentation.WebCamActivity
import com.example.patientManageApp.presentation.moveScreen
import com.example.patientManageApp.presentation.noRippleClickable

@Composable
fun HomeScreen(navController: NavHostController) {
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current

    BackOnPressed()

    Column(modifier = Modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "카메라 ",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(vertical = 20.dp, horizontal = 20.dp)
            )

            Button(onClick = {
                navController.navigate(AppScreen.SettingCamera.route) {
                    navController.graph.startDestinationRoute?.let {
                        popUpTo(it) { saveState = true }
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFc0c2c4),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .padding(end = 20.dp)
                    .indication(
                        interactionSource = interactionSource,
                        rememberRipple(color = Color(0xfff1f3f5))
                    ),
                shape = RoundedCornerShape(10.dp),
                interactionSource = interactionSource
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.add),
                        contentDescription = "add")

                    Text("카메라 추가", modifier = Modifier.padding(start = 5.dp))
                }
            }
        }

        Canvas(modifier = Modifier
            .fillMaxWidth()) {
            drawLine(
                Color(0xFFc0c2c4),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        }

        LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
            items(1, key = { it }) {
                Surface(modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth()
                    .shadow(5.dp, RoundedCornerShape(10.dp)),
                    shadowElevation = 10.dp,
                    color = Color(0xFFc0c2c4)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Camera",
                                modifier = Modifier.padding(
                                    start = 15.dp,
                                    top = 15.dp,
                                    bottom = 15.dp
                                ),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.settings),
                                contentDescription = "settings",
                                modifier = Modifier
                                    .padding(end = 15.dp)
                                    .noRippleClickable {
                                        moveScreen(navController, AppScreen.SettingCamera.route)
                                    }
                            )
                        }

                        Box(modifier = Modifier) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_background),
                                contentDescription = "thumbnail",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.FillWidth
                            )

                            Icon(
                                painter = painterResource(id = R.drawable.play),
                                contentDescription = "play",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .noRippleClickable {
                                        val intent = Intent(context, WebCamActivity::class.java)
                                        context.startActivity(intent)
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}