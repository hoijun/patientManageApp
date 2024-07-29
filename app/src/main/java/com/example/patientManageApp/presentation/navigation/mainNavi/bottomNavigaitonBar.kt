package com.example.patientManageApp.presentation.navigation.mainNavi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.patientManageApp.presentation.AppScreen
import com.example.patientManageApp.presentation.moveScreen

@Composable
fun MainBottomNavigation(
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