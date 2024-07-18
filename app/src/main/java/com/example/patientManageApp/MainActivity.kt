package com.example.patientManageApp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
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
import com.example.patientManageApp.Constant.Companion.BackOnPressed
import com.example.patientManageApp.Constant.Companion.DateBottomSheet
import com.example.patientManageApp.Constant.Companion.ScreenHeader
import com.example.patientManageApp.Constant.Companion.SubScreenHeader
import com.example.patientManageApp.Constant.Companion.innerShadow
import com.example.patientManageApp.Constant.Companion.moveScreen
import com.example.patientManageApp.Constant.Companion.noRippleClickable
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
                Main()
            }
        }
    }
}

@Composable
private fun Main() {
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        MainBottomNavigation(navController = navController)
    })
    {
        Box(modifier = Modifier.padding(bottom = it.calculateBottomPadding())) {
            MainNavHost(navController = navController, startDestination = AppScreen.Home.route)
        }
    }
}

@Composable
private fun MainNavHost(navController: NavHostController, startDestination: String) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            val currentIndex = initialState.destination.route?.let { getIndexForRoute(it)} ?: -1
            val targetIndex = targetState.destination.route?.let { getIndexForRoute(it) } ?: -1
            if (currentIndex == -1) {
                EnterTransition.None
            }
            else if (targetIndex == -1) {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(250))
            }
            else if (currentIndex < targetIndex) {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(100))
            } else {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(100))
            }
        },
        exitTransition = {
            val currentIndex = initialState.destination.route?.let { getIndexForRoute(it) } ?: -1
            val targetIndex = targetState.destination.route?.let { getIndexForRoute(it) } ?: -1
            if (currentIndex == -1 || targetIndex == -1) {
                ExitTransition.None
            }
            else if (targetIndex > currentIndex) {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(100))
            } else {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(100))
            }
        }
    ) {
        composable(route = AppScreen.Home.route) {
            HomeScreen(navController)
        }

        composable(route = AppScreen.SettingCamera.route) {
            SettingCameraScreen(navController = navController)
        }

        composable(route = AppScreen.Calendar.route) {
            CalendarScreen(navController)
        }

        composable(route = AppScreen.DetailCalendarInfo.route) {
            DetailCalendarInfoScreen(navController)
        }

        composable(route = AppScreen.Analysis.route) {
            AnalysisScreen()
        }

        composable(route = AppScreen.MyPage.route) {
            MyPageScreen(navController)
        }

        composable(route = AppScreen.UserProfile.route) {
            UserProfileScreen(navController)
        }

        composable(route = AppScreen.PatientProfile.route) {
            PatientProfileScreen(navController)
        }
    }
}

private fun getIndexForRoute(route: String): Int {
    return when (route) {
        AppScreen.Home.route -> 0
        AppScreen.Calendar.route -> 1
        AppScreen.Analysis.route -> 2
        AppScreen.MyPage.route -> 3
        else -> -1
    }
}

@Composable
private fun MainBottomNavigation(
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
        visible = items.map { it.route }.contains(currentRoute)) {
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
                       moveScreen(navController, item.route)
                    }
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(navController: NavHostController) {
    val interactionSource = remember { MutableInteractionSource() }

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
                    color = Color(0xFFc0c2c4)) {
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

                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingCameraScreen(navController: NavHostController) {
    var cameraName by remember { mutableStateOf("") }
    var rtspAddress by remember { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }
    val backPressedState by remember { mutableStateOf(true) }

    BackHandler(enabled = backPressedState) {
        moveScreen(navController, AppScreen.Home.route)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .imePadding()) {
        SubScreenHeader(pageName = "카메라 설정") {
            moveScreen(navController, AppScreen.Home.route)
        }
        OutlinedTextField(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 30.dp, end = 30.dp),
            value = cameraName,
            onValueChange = {
                cameraName = it
            }, label = {
                Text(text = "카메라 이름", color = Color.Gray)
            },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.DarkGray),
            singleLine = true)

        OutlinedTextField(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 15.dp),
            value = rtspAddress,
            onValueChange = {
                rtspAddress = it
            }, label = {
                Text(text = "카메라 RTSP 주소", color = Color.Gray)
            },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.DarkGray),
            singleLine = true)

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFc0c2c4),
                contentColor = Color.Black
            ),
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 30.dp)
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
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun CalendarScreen(navController: NavHostController) {
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

        Calendar.MonthHeader(state.firstVisibleMonth.yearMonth,
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

@Composable
private fun DetailCalendarInfoScreen(navController: NavHostController) {
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

@Composable
private fun AnalysisScreen() {
    BackOnPressed()

    var currentMonth by remember { mutableStateOf(LocalDate.now().yearMonth) }
    Column {
        ScreenHeader(pageName = "이상 행동 통계")

        Calendar.MonthHeader(month = currentMonth,
            onLeftClick = { currentMonth = currentMonth.previousMonth },
            onRightClick = { currentMonth = currentMonth.nextMonth })

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
                Color(0xFFc0c2c4),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        }

        SetAnalysisScreenInfo(R.drawable.warning, "이번 달 이상 행동 횟수", "22회")
        SetAnalysisScreenInfo(R.drawable.arrow_up, "지난달 대비 증가 횟수", "4회")
    }
}

@Composable
private fun SetAnalysisScreenInfo(icon: Int, text: String, text2: String) {
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
private fun MyPageScreen(navController: NavHostController) {
    BackOnPressed()
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
                        text = "정회준",
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
                    text = "ghlwns10@kakao.com",
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
                        moveScreen(navController, AppScreen.UserProfile.route)
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
            color = Color(0xFFc0c2c4)) {
        }

        SetMyPageScreenMenu("환자 정보 관리") {
            moveScreen(navController, AppScreen.PatientProfile.route)
        }
        SetMyPageScreenMenu("문의 하기") {

        }
        SetMyPageScreenMenu("개인 정보 처리 방침") {

        }
        SetMyPageScreenMenu("앱 사용법 보기") {

        }

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

@Composable
private fun UserProfileScreen(navController: NavHostController) {
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
            color = Color(0xFFc0c2c4)) {
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
            color = Color(0xFFc0c2c4)) {
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
            color = Color(0xFFc0c2c4)) {
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

@Composable
private fun PatientProfileScreen(navController: NavHostController) {
    val interactionSource = remember { MutableInteractionSource() }
    val backPressedState by remember { mutableStateOf(true) }
    var patientName by remember { mutableStateOf("") }
    var isAbleEditPatientName by remember { mutableStateOf(false) }
    var patientBirth by remember { mutableStateOf("") }
    var isAbleEditPatientBirth by remember { mutableStateOf(false) }
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    BackHandler(enabled = backPressedState) {
        moveScreen(navController, AppScreen.MyPage.route)
    }

    if (isBottomSheetOpen) {
        isAbleEditPatientBirth = true
        DateBottomSheet(modifier = Modifier, closeSheet = {
            patientBirth = it
            isBottomSheetOpen = false
        })
    }

    Column {
        SubScreenHeader(pageName = "환자 정보") {
            moveScreen(navController, AppScreen.MyPage.route)
        }

        Row(modifier = Modifier.padding(start = 20.dp, top = 20.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "이름", fontSize = 15.sp)
            OutlinedTextField(
                value = patientName, onValueChange = {
                    patientName = it
                }, modifier = Modifier
                    .padding(start = 15.dp)
                    .width(250.dp),
                enabled = isAbleEditPatientName,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.DarkGray),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.edit),
                contentDescription = "edit",
                modifier = Modifier
                    .noRippleClickable {
                        isAbleEditPatientName = !isAbleEditPatientName
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

        Row(modifier = Modifier.padding(start = 20.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "생일",
                fontSize = 15.sp,
                modifier = Modifier.padding(end = 15.dp)
            )
            Box(
                modifier = Modifier
                    .border(
                        1.dp, if (isAbleEditPatientBirth) Color.DarkGray else {
                            Color.LightGray
                        }, RoundedCornerShape(4.dp)
                    )
                    .width(250.dp)
                    .height(55.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = patientBirth,
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

        Spacer(modifier = Modifier.weight(1f))

        AnimatedVisibility(visible = patientBirth.isNotEmpty() or patientName.isNotEmpty()) {
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
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    PatientManageAppTheme {
        Main()
    }
}