package com.example.patientManageApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.patientManageApp.presenter.AppScreen
import com.example.patientManageApp.presenter.navigation.mainNavi.MainBottomNavigation
import com.example.patientManageApp.presenter.navigation.mainNavi.MainNavHost
import com.example.patientManageApp.presenter.theme.PatientManageAppTheme

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

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    PatientManageAppTheme {
        Main()
    }
}