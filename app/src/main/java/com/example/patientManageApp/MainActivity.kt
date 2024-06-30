package com.example.patientManageApp

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.LocaleData
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
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
    }
    ) {
        Box(modifier = Modifier.padding(it)) {
            MyNavHost(navController = navController, startDestination = AppScreen.Home.route)
        }
    }
}

@Composable
private fun MyNavHost(navController: NavHostController, startDestination: String) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xfff1f3f5)) {}
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400)) }
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
        Box(
            Modifier
                .fillMaxWidth()
                .background(Color(0xfff1f3f5))
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(15.dp, 15.dp)
                )
        ) {
            BottomNavigation(
                modifier = Modifier,
                backgroundColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                items.forEach { item ->
                    BottomNavigationItem(
                        modifier = Modifier
                            .padding(5.dp)
                            .padding(horizontal = 5.dp)
                            .width(24.dp)
                            .background(Color.Transparent, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp)),
                        selected = currentRoute == item.route,
                        selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
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
}

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(vertical = 100.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "카메라를 선택해 주세요!", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Button( onClick = {
            val webCamActivityIntent = Intent(context, WebCamActivity::class.java)
            context.startActivity(webCamActivityIntent)
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 5.dp
            ),
            shape = RoundedCornerShape(30),
            modifier = Modifier
                .width(150.dp)
                .height(50.dp)
        ) {
            Text(text = "1번 카메라", fontSize = 18.sp)
        }

        Button( onClick = {

        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 5.dp
            ),
            shape = RoundedCornerShape(30),
            modifier = Modifier
                .width(150.dp)
                .height(50.dp)
        ) {
            Text(text = "2번 카메라", fontSize = 18.sp)
        }

        Button( onClick = {

        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 5.dp
            ),
            shape = RoundedCornerShape(30),
            modifier = Modifier
                .width(150.dp)
                .height(50.dp)
        ) {
            Text(text = "3번 카메라", fontSize = 18.sp)
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CalendarScreen() {
    val currentMonth = YearMonth.now()
    val startMonth = currentMonth.minusMonths(100) // Adjust as needed
    val endMonth = currentMonth.plusMonths(0)// Adjust as needed
    val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)
    val selectedDate = mutableStateOf<LocalDate?>(LocalDate.now())

    val coroutineScope = rememberCoroutineScope()

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfGrid
    )

    if (state.firstVisibleMonth.yearMonth.toString() != LocalDate.now().yearMonth.toString()) {
        selectedDate.value = state.firstVisibleMonth.yearMonth.atDay(1)
    }

    Column {
        Calendar.MonthHeader(state.firstVisibleMonth,
            onLeftClick = { coroutineScope.launch { state.scrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth) }},
            onRightClick = { coroutineScope.launch { state.scrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth) }}
        )

        HorizontalCalendar(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
            state = state,
            dayContent = { day1 ->
                Calendar.Day(day1, isSelected = selectedDate.value == day1.date) { day2 ->
                    if (selectedDate.value != day2.date) {
                        selectedDate.value = if (selectedDate.value == day2.date) null else day2.date
                    }
                }
            },
            monthHeader = {
                DaysOfWeekTitle(
                    modifier = Modifier.padding(vertical = 10.dp),
                    daysOfWeek = daysOfWeek
                )
            }
        )
    }
}

@Composable
fun AnalysisScreen() {
    Text(text = "Analysis")
}

@Composable
fun MyPageScreen() {
    Text(text = "MyPage")
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    PatientManageAppTheme {
        MyApp()
    }
}