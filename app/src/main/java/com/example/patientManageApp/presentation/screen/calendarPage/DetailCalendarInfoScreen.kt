package com.example.patientManageApp.presentation.screen.calendarPage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.patientManageApp.presentation.AppScreen
import com.example.patientManageApp.R
import com.example.patientManageApp.presentation.SubScreenHeader
import com.example.patientManageApp.presentation.moveScreen

@Composable
fun DetailCalendarInfoScreen(navController: NavHostController) {
    val backPressedState by remember { mutableStateOf(true) }
    BackHandler(enabled = backPressedState) {
        moveScreen(navController, AppScreen.Calendar.route)
    }

    Column {
        SubScreenHeader(pageName = "이상 행동 기록") {
            moveScreen(navController, AppScreen.Calendar.route)
        }

        Row(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .align(Alignment.CenterHorizontally)
                .height(IntrinsicSize.Min)
        ) {
            var iconHeight = 0f
            Icon(painter = painterResource(id = R.drawable.time),
                contentDescription = "time",
                modifier = Modifier
                    .onGloballyPositioned {
                        iconHeight = it.size.height.toFloat()
                    }
                    .size(30.dp)
            )
            Canvas(
                modifier = Modifier
                    .padding(start = 10.dp)
            ) {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, iconHeight)
                }
                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(
                        3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }

            Text(
                text = "0000년 00월 00일",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxHeight()
                    .wrapContentSize()
            )
        }

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .padding(start = 30.dp, end = 30.dp)
        )

        Text(
            text = "이상 행동 자료",
            modifier = Modifier
                .padding(top = 5.dp, bottom = 20.dp, end = 30.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            fontSize = 10.sp,
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

        Row(
            modifier = Modifier
                .padding(start = 30.dp, top = 30.dp)
                .height(intrinsicSize = IntrinsicSize.Min)
        ) {
            var iconHeight = 0f
            Icon(painter = painterResource(id = R.drawable.time2),
                contentDescription = "time",
                modifier = Modifier.onGloballyPositioned {
                    iconHeight = it.size.height.toFloat()
                })
            Canvas(
                modifier = Modifier
                    .padding(start = 10.dp)
            ) {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, iconHeight)
                }
                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(
                        3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }

            Text(
                text = "오전 00시 00분",
                modifier = Modifier
                    .padding(start = 10.dp)
                    .align(Alignment.CenterVertically),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Row(
            modifier = Modifier
                .padding(start = 30.dp, top = 30.dp)
                .height(intrinsicSize = IntrinsicSize.Min)
        ) {
            var iconHeight = 0f
            Icon(painter = painterResource(id = R.drawable.info),
                contentDescription = "time",
                modifier = Modifier.onGloballyPositioned {
                    iconHeight = it.size.height.toFloat()
                })
            Canvas(
                modifier = Modifier
                    .padding(start = 10.dp)
            ) {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, iconHeight)
                }
                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(
                        3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }

            Text(
                text = "낙상",
                modifier = Modifier
                    .padding(start = 10.dp)
                    .align(Alignment.CenterVertically),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}