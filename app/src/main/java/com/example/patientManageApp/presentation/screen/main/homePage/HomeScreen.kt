package com.example.patientManageApp.presentation.screen.main.homePage

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.patientManageApp.R
import com.example.patientManageApp.domain.entity.CameraEntity
import com.example.patientManageApp.presentation.AppScreen
import com.example.patientManageApp.presentation.BackOnPressed
import com.example.patientManageApp.presentation.WebCamActivity
import com.example.patientManageApp.presentation.moveScreenWithArgs
import com.example.patientManageApp.presentation.noRippleClickable
import com.example.patientManageApp.presentation.screen.main.MainViewModel
import com.google.gson.Gson

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController, viewModel: MainViewModel) {
    val cameraList by viewModel.cameraData.collectAsState()
    BackOnPressed()
    HomeScreen(cameraList) { cameraEntity ->
        val cameraEntityJsonString = Gson().toJson(cameraEntity)
        moveScreenWithArgs(navController, "${AppScreen.SettingCamera.route}/${Uri.encode(cameraEntityJsonString)}")
    }
}

@Composable
private fun HomeScreen(cameraList: List<CameraEntity>, onSettingBtnClick: (cameraEntity: CameraEntity) -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeScreenHeader(cameraList.size) { onSettingBtnClick(CameraEntity()) }
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFc0c2c4))
        CameraItem(cameraList) { cameraEntity ->
            onSettingBtnClick(cameraEntity)
        }
    }

    if(cameraList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("카메라를 추가 해주세요!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun HomeScreenHeader(cameraCount: Int, onSettingBtnClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
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

        if (cameraCount < 4) {
            Button(
                onClick = {
                    onSettingBtnClick()
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
                shape = RoundedCornerShape(15.dp),
                interactionSource = interactionSource
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "add"
                    )

                    Text("카메라 추가", modifier = Modifier.padding(start = 5.dp))
                }
            }
        }
    }
}

@Composable
private fun CameraItem(cameraList: List<CameraEntity>, onSettingBtnClick: (cameraEntity: CameraEntity) -> Unit) {
    val context = LocalContext.current
    LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
        items(items = cameraList, key = { it.name }) {
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
                            cameraList[cameraList.indexOf(it)].name,
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
                                    onSettingBtnClick(cameraList[cameraList.indexOf(it)])
                                }
                        )
                    }

                    Box(modifier = Modifier) {
                        Image(
                            painter = painterResource(id = getBackGroundImage(
                                cameraList[cameraList.indexOf(it)].backGroundImg
                            )),
                            contentDescription = "thumbnail",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                        )

                        Button(
                            onClick = {
                                val intent = Intent(context, WebCamActivity::class.java).apply {
                                    putExtra("rtspAddress", cameraList[cameraList.indexOf(it)].rtspAddress)
                                    putExtra("cameraName", cameraList[cameraList.indexOf(it)].name)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFc0c2c4).copy(0.85f),
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .align(Alignment.Center),
                            shape = RoundedCornerShape(20.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.play),
                                    modifier = Modifier.size(25.dp),
                                    contentDescription = "play"
                                )

                                Text("카메라 재생", modifier = Modifier.padding(start = 10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getBackGroundImage(name: String): Int {
    return when (name) {
        "livingRoom" -> R.drawable.living_room
        "bedRoom" -> R.drawable.bed_room
        "myRoom" -> R.drawable.my_room
        "kitchenRoom" -> R.drawable.kitchen_room
        else -> R.drawable.ic_launcher_background
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        listOf(
            CameraEntity("test", "test", "livingRoom"),
            CameraEntity("test1", "test1", "livingRoom")
        ),
        onSettingBtnClick = {})
}