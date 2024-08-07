package com.example.patientManageApp.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.patientManageApp.presentation.navigation.loginNavi.LoginNavHost
import com.example.patientManageApp.presentation.theme.PatientManageAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val showSnackBar = intent.getBooleanExtra("ShowSnackBar", false)
        val snackBarMessage = intent.getStringExtra("SnackBarMessage") ?: ""
        setContent {
            PatientManageAppTheme {
                Login(showSnackBar, snackBarMessage)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Login(showSnackBar: Boolean, snackBarMessage: String) {
    val snackBarHostState = remember { SnackbarHostState() }
    if (showSnackBar) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
        ) {
            LaunchedEffect(snackBarHostState) {
                snackBarHostState.showSnackbar(
                    message = snackBarMessage,
                    actionLabel = "닫기",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    val navController = rememberNavController()
    LoginNavHost(navController = navController, startDestination = LoginAppScreen.Login.route)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PatientManageAppTheme {
        Login(false, "snackBarMessage")
    }
}