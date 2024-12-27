package com.example.patientManageApp.presentation

import android.content.Intent
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.patientManageApp.presentation.theme.PatientManageAppTheme
import com.example.patientManageApp.presentation.screen.webCamPage.WebCamScreen

class WebCamActivity : ComponentActivity() {

    private val onBackPressed = {
        window.insetsController?.show(WindowInsets.Type.statusBars())
        window.insetsController?.show(WindowInsets.Type.navigationBars())
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.insetsController?.hide(WindowInsets.Type.statusBars())
        window.insetsController?.hide(WindowInsets.Type.navigationBars())
        window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        val rtspUrl = intent.getStringExtra("rtspAddress") ?: ""
        val cameraName = intent.getStringExtra("cameraName") ?: ""
        setContent {
            PatientManageAppTheme {
                WebCamScreen(rtspUrl, cameraName, onBackPressed)
            }
        }
    }
}