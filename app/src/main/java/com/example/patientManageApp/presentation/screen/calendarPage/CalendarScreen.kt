package com.example.patientManageApp.presentation.screen.calendarPage

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.patientManageApp.presentation.AppScreen
import com.example.patientManageApp.presentation.BackOnPressed
import com.example.patientManageApp.R
import com.example.patientManageApp.presentation.ScreenHeader
import com.example.patientManageApp.presentation.moveScreen
import com.example.patientManageApp.presentation.noRippleClickable
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@SuppressLint("UnrememberedMutableState")
@Composable
fun CalendarScreen(navController: NavHostController) {
    BackOnPressed()

    val currentMonth = YearMonth.now()
    val startMonth = YearMonth.of(2023, 1)
    val endMonth = LocalDate.now().yearMonth
    val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    val coroutineScope = rememberCoroutineScope()

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfGrid
    )

    LaunchedEffect(Unit) {
        state.scrollToMonth(currentMonth)
    }

    LaunchedEffect(state.firstVisibleMonth.yearMonth) {
        selectedDate = if (state.firstVisibleMonth.yearMonth == YearMonth.from(LocalDate.now())) {
            LocalDate.now()
        } else {
            state.firstVisibleMonth.yearMonth.atDay(1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(pageName = "이상 행동 달력")

        MonthHeader(state.firstVisibleMonth.yearMonth,
            onLeftClick = { coroutineScope.launch { state.scrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth) } },
            onRightClick = { coroutineScope.launch { state.scrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth) } }
        )

        HorizontalCalendar(
            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 5.dp),
            state = state,
            dayContent = { day  ->
                Day(day, isSelected = selectedDate == day.date) { clickedDay ->
                    if (selectedDate != clickedDay.date) {
                        selectedDate = if (selectedDate == clickedDay.date) null else clickedDay.date
                    }
                }
            },
            monthHeader = {
                DaysOfWeekTitle(
                    modifier = Modifier.padding(bottom = 10.dp),
                    daysOfWeek = daysOfWeek
                )
            }
        )

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp)) {
            drawLine(
                Color(0xFFc0c2c4),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        }

        Text(
            text = "${selectedDate?.monthValue}월 ${selectedDate?.dayOfMonth}일",
            modifier = Modifier.padding(start = 15.dp, top = 10.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
        )

        LazyColumn(modifier = Modifier.padding(top = 20.dp, start = 25.dp, bottom = 2.5.dp),
            contentPadding = PaddingValues(top = 5.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp)) {
            items(10, key = { it }) {
                Row(modifier = Modifier.noRippleClickable {
                    moveScreen(navController, AppScreen.DetailCalendarInfo.route)
                }) {
                    var iconHeight = 0f
                    Icon(painter = painterResource(id = R.drawable.time),
                        contentDescription = "time",
                        modifier = Modifier.onGloballyPositioned {
                            iconHeight = it.size.height.toFloat()
                        })
                    Canvas(modifier = Modifier
                        .padding(start = 20.dp)) {
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
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(text = "오전 0시 0분", fontSize = 13.sp)
                        Text(text = "낙상", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}