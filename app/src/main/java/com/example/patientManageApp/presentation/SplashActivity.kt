package com.example.patientManageApp.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.patientManageApp.presentation.screen.splashPage.SplashScreen
import com.example.patientManageApp.presentation.theme.PatientManageAppTheme
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PatientManageAppTheme {
                SplashScreen()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    PatientManageAppTheme {
        SplashScreen()
    }
}