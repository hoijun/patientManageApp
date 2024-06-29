package com.example.patientManageApp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.patientManageApp.ui.theme.PatientManageAppTheme
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import io.socket.engineio.parser.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class WebCamActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PatientManageAppTheme {
                WebCamScreen()
            }
        }
    }
}

@Composable
fun WebCamScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) { }
    var socket by remember { mutableStateOf<Socket?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val opts = IO.Options()
            opts.transports = arrayOf(WebSocket.NAME)
            socket = IO.socket("http://192.168.35.77:5001", opts) // 서버의 IP 주소로 변경
            socket?.connect()
        } catch (e: Exception) {
            errorMessage = "소켓 연결 실패: ${e.message}"
        }
    }

    LaunchedEffect(socket) {
        socket?.on("frame") { args ->
            try {
                val data = args[0] as JSONObject
                val imageStr = data.getString("image")
                coroutineScope.launch(Dispatchers.Default) {
                    val imageBytes = Base64.decode(imageStr, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    imageBitmap = bitmap
                }
            } catch (e: Exception) {
                errorMessage = "이미지 처리 실패: ${e.message}"
            }
        }

        socket?.on(Socket.EVENT_CONNECT) {
            coroutineScope.launch {
                errorMessage = null
                println("연결 성공")
            }
        }

        socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
            coroutineScope.launch {
                errorMessage = "연결 오류 발생: ${args[0]}"
                println("연결 오류: ${args[0]}")
            }
        }

        socket?.on(Socket.EVENT_DISCONNECT) {
            coroutineScope.launch {
                errorMessage = "서버 연결 끊김"
                println("서버 연결 끊김")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column (modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            Text(modifier = Modifier.rotate(90f),
                text = "1번 카메라",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        imageBitmap?.let { bitmap ->
            Image(bitmap = bitmap.asImageBitmap(),
                contentDescription = "Video Stream",
                modifier = Modifier.fillMaxSize()
            )
        }

        errorMessage?.let { error ->
            androidx.compose.material.Text(
                text = error,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.White)
                    .padding(16.dp)
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            socket?.disconnect()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WebCamScreenPreview() {
    PatientManageAppTheme {
        WebCamScreen()
    }
}