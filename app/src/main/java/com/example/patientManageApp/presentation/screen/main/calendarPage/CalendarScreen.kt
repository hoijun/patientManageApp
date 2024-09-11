package com.example.patientManageApp.presentation.screen.main.calendarPage

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.patientManageApp.R
import com.example.patientManageApp.domain.entity.OccurrencesEntity
import com.example.patientManageApp.presentation.AppScreen
import com.example.patientManageApp.presentation.BackOnPressed
import com.example.patientManageApp.presentation.CustomDivider
import com.example.patientManageApp.presentation.CustomVerticalDivider
import com.example.patientManageApp.presentation.ScreenHeader
import com.example.patientManageApp.presentation.moveScreenWithArgs
import com.example.patientManageApp.presentation.noRippleClickable
import com.example.patientManageApp.presentation.screen.main.MainViewModel
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
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun CalendarScreen(navController: NavHostController, date: String = "bottom", mainViewModel: MainViewModel) {
    BackOnPressed()
    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            mainViewModel.getUserData()
            isRefreshing = false
        },
    )

    val currentMonth = if (date == "bottom") YearMonth.now() else LocalDate.parse(date).yearMonth
    val selectedDay = if (date == "bottom") LocalDate.now() else LocalDate.parse(date)
    val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var isMoveBottom by remember { mutableStateOf(date == "bottom") }
    val occurrenceDays = mainViewModel.occurrenceData

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullToRefreshState)
    ) {
        CalendarScreen(
            isMoveBottom = isMoveBottom,
            occurrenceDays = occurrenceDays,
            selectedDay = selectedDay,
            currentMonth = currentMonth,
            daysOfWeek = daysOfWeek,
            formatter = formatter,
            onItemClick = {
                isMoveBottom = false
                moveScreenWithArgs(
                    navController,
                    "${AppScreen.DetailCalendarInfo.route}/${Uri.encode(it)}"
                )
            }
        )

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullToRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color.White,
            contentColor = Color.Black
        )
    }
}

@Composable
private fun CalendarScreen(
    isMoveBottom: Boolean,
    occurrenceDays: HashMap<String, List<OccurrencesEntity>>,
    selectedDay: LocalDate,
    currentMonth: YearMonth,
    daysOfWeek: List<DayOfWeek>,
    formatter: DateTimeFormatter,
    onItemClick: (date: String) -> Unit
) {
    var isMoveBottomNavi by remember { mutableStateOf(isMoveBottom) }
    var thisMonth by remember { mutableStateOf(currentMonth) }
    var selectedDate by remember { mutableStateOf(selectedDay) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val koreanDateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    val calendarState = rememberCalendarState(
        startMonth = YearMonth.of(2023, 1),
        endMonth = LocalDate.now().yearMonth,
        firstVisibleMonth = thisMonth,
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfGrid
    )

    val occurrenceDaysToLocalDate by remember(calendarState.firstVisibleMonth.yearMonth) {
        mutableStateOf(
            occurrenceDays.keys
                .map { LocalDate.parse(it, formatter) }
                .filter { it.yearMonth == calendarState.firstVisibleMonth.yearMonth }
                .map { it.dayOfYear }
                .sorted()
        )
    }

    if (isMoveBottomNavi) {
        LaunchedEffect(Unit) {
            calendarState.scrollToMonth(thisMonth)
        }
    }

    LaunchedEffect(calendarState.firstVisibleMonth.yearMonth) {
        if (isMoveBottomNavi) {
            thisMonth = LocalDate.now().yearMonth
            selectedDate =
                if (calendarState.firstVisibleMonth.yearMonth == YearMonth.from(thisMonth)) {
                    LocalDate.now()
                } else {
                    calendarState.firstVisibleMonth.yearMonth.atDay(1)
                }
        } else {
            isMoveBottomNavi = true
        }
    }

    LaunchedEffect(selectedDate) {
        listState.scrollToItem(0)
    }

    Column(
        modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
        ScreenHeader(pageName = "이상 행동 달력")

        MonthHeader(
            calendarState.firstVisibleMonth.yearMonth,
            onLeftClick = {
                coroutineScope.launch {
                    calendarState.scrollToMonth(calendarState.firstVisibleMonth.yearMonth.previousMonth)
                }
            },
            onRightClick = {
                coroutineScope.launch {
                    calendarState.scrollToMonth(calendarState.firstVisibleMonth.yearMonth.nextMonth)
                }
            }
        )

        HorizontalCalendar(
            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 5.dp),
            state = calendarState,
            dayContent = { day ->
                Day(
                    day, isSelected = selectedDate.dayOfYear == day.date.dayOfYear,
                    occurrenceDay = occurrenceDaysToLocalDate
                ) { clickedDay ->
                    if (selectedDate != clickedDay.date) {
                        selectedDate = clickedDay.date
                    }
                }
            },
            monthHeader = {
                DaysOfWeekTitle(
                    modifier = Modifier.padding(bottom = 10.dp),
                    daysOfWeek = daysOfWeek
                )
            },
            userScrollEnabled = false
        )

        CustomDivider(horizontal = 0.dp, vertical = 0.dp)

        Text(
            text = "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일",
            modifier = Modifier.padding(start = 15.dp, top = 10.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
        )

        OccurrencesColumn(
            modifier = Modifier.weight(1f),
            occurrences = occurrenceDays[selectedDate.format(formatter)] ?: emptyList(),
            state = listState
        ) {
            onItemClick((selectedDate.format(koreanDateFormatter) ?: "") + "/" + it)
        }
    }
}

@Composable
private fun OccurrencesColumn(
    modifier: Modifier,
    occurrences: List<OccurrencesEntity>,
    state : LazyListState,
    onItemClick: (time: String) -> Unit
) {
    if (occurrences.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Text(
                text = "이상 행동이 없습니다.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        LazyColumn(
            state = state,
            modifier = modifier
                .padding(top = 20.dp, start = 25.dp, bottom = 2.5.dp),
            contentPadding = PaddingValues(top = 5.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp),
        ) {
            items(
                occurrences.size,
                key = { it }
            ) {
                Row(modifier = Modifier.noRippleClickable {
                    onItemClick(occurrences[it].time + "/" + occurrences[it].kind)
                }) {
                    var iconHeight by remember { mutableFloatStateOf(0f) }
                    Icon(painter = painterResource(id = R.drawable.time),
                        contentDescription = "time",
                        modifier = Modifier.onGloballyPositioned {
                            iconHeight = it.size.height.toFloat()
                        }
                    )
                    CustomVerticalDivider(iconHeight = iconHeight)
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(text = occurrences[it].time, fontSize = 13.sp)
                        Text(
                            text = occurrences[it].kind,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarScreenPreview() {
    val occurrenceDays = hashMapOf(
        "2024-08-14" to listOf(
            OccurrencesEntity("09:00:24", "낙상"),
            OccurrencesEntity("10:00:53", "식사"),
            OccurrencesEntity("23:00:23", "낙상"),
            OccurrencesEntity("09:00:24", "낙상"),
            OccurrencesEntity("23:00:23", "낙상"),
            OccurrencesEntity("09:00:24", "낙상")
        ),
        "2024-08-13" to listOf(
            OccurrencesEntity("11:00:24", "낙상"),
            OccurrencesEntity("21:00:53", "낙상")
        ),
        "2024-07-15" to listOf(
            OccurrencesEntity("09:00:34", "낙상"),
            OccurrencesEntity("20:00:43", "낙상")
        )
    )

    CalendarScreen(
        isMoveBottom = true,
        occurrenceDays = occurrenceDays,
        selectedDay = LocalDate.now(),
        currentMonth = YearMonth.now(),
        daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY),
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        onItemClick = {}
    )
}