package com.example.patientManageApp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.patientManageApp.Calendar.Companion.DaysOfWeekTitle
import com.example.patientManageApp.ui.theme.PatientManageAppTheme
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.core.yearMonth
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberTopAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberEndAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PatientManageAppTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        MyBottomNavigation(navController = navController)
    })
    {
        Box(modifier = Modifier.padding(bottom = it.calculateBottomPadding())) {
            MyNavHost(navController = navController, startDestination = AppScreen.Home.route)
        }
    }
}

@Composable
private fun MyNavHost(navController: NavHostController, startDestination: String) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = AppScreen.Home.route) {
            HomeScreen()
        }

        composable(route = AppScreen.Calendar.route) {
            CalendarScreen()
        }

        composable(route = AppScreen.Analysis.route) {
            AnalysisScreen()
        }

        composable(route = AppScreen.MyPage.route) {
            MyPageScreen()
        }
    }
}

@Composable
fun MyBottomNavigation(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        AppScreen.Home,
        AppScreen.Calendar,
        AppScreen.Analysis,
        AppScreen.MyPage
    )

    AnimatedVisibility(
        visible = items.map { it.route }.contains(currentRoute)
    ) {
        BottomNavigation(
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp, 15.dp))
                .shadow(10.dp),
            backgroundColor = Color.White,
            ) {
            items.forEach { item ->
                BottomNavigationItem(
                    modifier = Modifier
                        .padding(5.dp)
                        .padding(horizontal = 5.dp)
                        .width(24.dp)
                        .background(Color.Transparent)
                        .clip(RoundedCornerShape(16.dp)),
                    selected = currentRoute == item.route,
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.Gray,
                    label = {
                        Text(
                            text = stringResource(id = item.title),
                            style = TextStyle(fontSize = 12.5.sp)
                        )
                    },
                    icon = {
                        Icon(
                            modifier = Modifier.padding(bottom = 5.dp),
                            painter = painterResource(id = item.icon),
                            contentDescription = stringResource(id = item.title)
                        )
                    },
                    onClick = {
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let {
                                popUpTo(it) { saveState = true }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreen() {
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
                fontSize = 20.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(vertical = 20.dp, horizontal = 20.dp)
            )

            Button(onClick = { }, 
                colors = ButtonColors(Color.LightGray, Color.Black, Color.LightGray, Color.Black),
                modifier = Modifier.padding(end = 20.dp)
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
                Color.Black,
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
                    color = Color.LightGray) {
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
                            IconButton(onClick = { },
                                modifier = Modifier.size(45.dp)) {
                                Icon(
                                    painter = painterResource(id = R.drawable.settings),
                                    contentDescription = "settings",
                                    modifier = Modifier.padding(end = 15.dp)
                                )
                            }
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

                            IconButton(onClick = {
                                val intent = Intent(context, WebCamActivity::class.java)
                                context.startActivity(intent)
                            },
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(45.dp)) {
                                Icon(painter = painterResource(id = R.drawable.play),
                                    contentDescription = "play",)
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CalendarScreen() {
    BackOnPressed()

    val currentMonth = YearMonth.now()
    val startMonth = YearMonth.of(2023, 1) // Adjust as needed
    val endMonth = LocalDate.now().yearMonth// Adjust as needed
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
        Text(text = "이상 행동 달력",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 20.dp)
                .fillMaxWidth())

        Canvas(modifier = Modifier
            .fillMaxWidth()) {
            drawLine(
                Color.Black,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        }

        Calendar.MonthHeader(state.firstVisibleMonth,
            onLeftClick = { coroutineScope.launch { state.scrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth) } },
            onRightClick = { coroutineScope.launch { state.scrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth) } }
        )

        HorizontalCalendar(
            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 5.dp),
            state = state,
            dayContent = { day  ->
                Calendar.Day(day, isSelected = selectedDate == day.date) { clickedDay ->
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
                Color.Black,
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
                Row {
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



@Composable
fun AnalysisScreen() {
    BackOnPressed()

    var currentMonth by remember { mutableStateOf(LocalDate.now().yearMonth) }
    val minMonth = YearMonth.of(2023, 1)
    val maxMonth = LocalDate.now().yearMonth
    Column {
        Text(
            text = "이상 행동 통계",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 20.dp)
                .fillMaxWidth()
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            drawLine(
                Color.Black,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min)
                .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { currentMonth = currentMonth.previousMonth },
                enabled = currentMonth > minMonth
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(align = Alignment.CenterVertically),
                text = "${currentMonth.year}년 ${currentMonth.month.value}월",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { currentMonth = currentMonth.nextMonth },
                enabled = currentMonth < maxMonth
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_right),
                    contentDescription = null
                )
            }
        }

        Chart(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .fillMaxHeight(0.5f),
            chart = columnChart(
                columns = listOf(
                    lineComponent(
                        color = Color.Gray,
                        thickness = 10.dp,
                        shape = Shapes.roundedCornerShape(topLeftPercent = 30, topRightPercent = 30)
                    )
                ),
                dataLabel = TextComponent.Builder().build().apply {
                    margins.set(2f)
                    color = Color.Black.toArgb()
                },
                axisValuesOverrider = AxisValuesOverrider.fixed(minY = 0f, maxY = 15f)
            ),
            chartModelProducer = ChartEntryModelProducer(
                listOf(
                    entryOf(x = 0f, y = 4f),
                    entryOf(x = 1f, y = 5f),
                    entryOf(x = 2f, y = 9f),
                    entryOf(x = 3f, y = 4f)
                )
            ),
            startAxis = rememberStartAxis(
                itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 4)
            ),
            endAxis = rememberEndAxis(
                itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 4)
            ),
            topAxis = rememberTopAxis(
                valueFormatter = { value, _ ->
                    val xAxisLabelData = listOf("1주", "2주", "3주", "4주")
                    (xAxisLabelData[value.toInt()])
                }
            ),
            bottomAxis = rememberBottomAxis(
                valueFormatter = { value, _ ->
                    val xAxisLabelData = listOf("1주", "2주", "3주", "4주")
                    (xAxisLabelData[value.toInt()])
                }
            ),
            runInitialAnimation = false
        )

        Row(modifier = Modifier.padding(start = 45.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(20.dp)
            ) {
                Canvas(
                    modifier = Modifier.size(12.dp)
                ) {
                    drawCircle(
                        color = Color.Gray,
                        radius = 6.dp.toPx(),
                        center = Offset(6.dp.toPx(), 6.dp.toPx())
                    )
                }
                Text(
                    text = "이상 행동 횟수(회)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
        ) {
            drawLine(
                Color.Black,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        }

        SetAnalysisInfo(R.drawable.warning, "이번 달 이상 행동 횟수", "22회")
        SetAnalysisInfo(R.drawable.arrow_up, "지난달 대비 증가 횟수", "4회")
    }
}

@Composable
fun SetAnalysisInfo(icon: Int, text: String, text2: String) {
    Row(
        modifier = Modifier
            .padding(start = 20.dp, top = 10.dp)
    ) {
        var iconHeight = 0f
        Icon(painter = painterResource(id = icon),
            contentDescription = "arrow_up",
            modifier = Modifier.onGloballyPositioned {
                iconHeight = it.size.height.toFloat()
            })
        Canvas(
            modifier = Modifier
                .padding(start = 20.dp)
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
        Column(modifier = Modifier.padding(start = 15.dp, bottom = 10.dp)) {
            Text(text = text, fontSize = 15.sp)
            Text(text = text2, fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.5.dp))
        }
    }
}

@Composable
fun MyPageScreen() {
    BackOnPressed()
    Text(text = "MyPage")
}

@Composable
fun BackOnPressed() {
    val context = LocalContext.current
    var backPressedState by remember { mutableStateOf(true) }
    var backPressedTime = 0L

    BackHandler(enabled = backPressedState) {
        if(System.currentTimeMillis() - backPressedTime < 2500) {
            // 앱 종료
            (context as Activity).finish()
        } else {
            backPressedState = true
            Toast.makeText(context, "한 번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}


@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    PatientManageAppTheme {
        MyApp()
    }
}