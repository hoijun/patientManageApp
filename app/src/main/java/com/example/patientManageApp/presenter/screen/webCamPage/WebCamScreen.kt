package com.example.patientManageApp.presenter.screen.webCamPage

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.patientManageApp.PlayerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun WebCamScreen(rtspUrl: String, onBackPressed: () -> Unit) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var playerState by remember { mutableStateOf(PlayerState.INITIALIZING) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val exoPlayer = remember { ExoPlayer.Builder(context).build().apply {
        volume = 0f
        addListener(object : Player.Listener {
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                playerState = PlayerState.ERROR
                errorMessage = "플레이어 오류: ${error.message}"
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_READY -> playerState = PlayerState.READY
                    Player.STATE_BUFFERING -> playerState = PlayerState.BUFFERING
                    Player.STATE_ENDED -> playerState = PlayerState.ENDED
                    Player.STATE_IDLE -> { }
                }
            }
        })
    } }

    val mediaSource = remember {
        RtspMediaSource.Factory()
            .setForceUseRtpTcp(true)
            .setDebugLoggingEnabled(true)
            .createMediaSource(MediaItem.fromUri(Uri.parse(rtspUrl)))
    }

    LaunchedEffect(mediaSource) {
        playerState = PlayerState.INITIALIZING
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        scope.launch {
            delay(10000)
            if (playerState == PlayerState.INITIALIZING) {
                playerState = PlayerState.ERROR
                errorMessage = "연결 시간 초과. 네트워크를 확인해 주세요."
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        when (playerState) {
            PlayerState.INITIALIZING, PlayerState.BUFFERING -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            PlayerState.READY -> {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                            useController = false
                            rotation = 180f
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            PlayerState.ERROR -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("오류 발생")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage ?: "알 수 없는 오류")
                }
            }

            PlayerState.ENDED -> {
                Text("재생 종료", modifier = Modifier.align(Alignment.Center))
            }
        }
        IconButton(
            onClick = onBackPressed,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 10.dp, end = 10.dp)
                .rotate(180f)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로 가기",
                tint = Color.White
            )
        }

        // "1번 카메라" 텍스트
        Text(
            text = "1번 카메라",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .rotate(180f)
        )
    }
}