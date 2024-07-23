package com.example.patientManageApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.patientManageApp.presenter.LoginAppScreen
import com.example.patientManageApp.presenter.navigation.loginNavi.LoginNavHost
import com.example.patientManageApp.presenter.theme.PatientManageAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PatientManageAppTheme {
                Login()
            }
        }
    }
}

@Composable
fun Login() {
    val navController = rememberNavController()
    LoginNavHost(navController = navController, startDestination = LoginAppScreen.Login.route)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PatientManageAppTheme {
        Login()
    }
}