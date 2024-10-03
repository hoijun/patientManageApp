package com.example.patientManageApp.presentation.screen.main.detailCalendarInfoPage

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.example.patientManageApp.R
import com.example.patientManageApp.presentation.AppScreen
import com.example.patientManageApp.presentation.CustomDivider
import com.example.patientManageApp.presentation.CustomVerticalDivider
import com.example.patientManageApp.presentation.LoadingDialog
import com.example.patientManageApp.presentation.SubScreenHeader
import com.example.patientManageApp.presentation.moveScreenWithArgs
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailCalendarInfoScreen(
    navController: NavHostController,
    dateAndTime: String,
    detailCalendarInfoViewModel: DetailCalendarInfoViewModel = hiltViewModel()
) {
    val backPressedState by remember { mutableStateOf(true) }
    val detailCalendarInfoUiState by detailCalendarInfoViewModel.detailCalendarInfoUiState.collectAsState()
    val mp4Uri by detailCalendarInfoViewModel.mp4Uri.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    val date = dateAndTime.split("/")[0]
    val time = dateAndTime.split("/")[1]
    val kind = dateAndTime.split("/")[2]

    BackHandler(enabled = backPressedState) {
        detailCalendarInfoViewModel.isIdle()
        snackBarHostState.currentSnackbarData?.dismiss()
        moveScreenWithArgs(
            navController,
            "${AppScreen.Calendar.route}/${Uri.encode(dateConvert(date))}"
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) {
        when (detailCalendarInfoUiState) {
            DetailCalendarInfoUiState.Idle -> {
                detailCalendarInfoViewModel.getOccurrenceMp4(
                    dateConvert(date) + "_" + time.replace(":", "")
                )
            }

            DetailCalendarInfoUiState.Error -> {
                LaunchedEffect(snackBarHostState) {
                    snackBarHostState.showSnackbar(
                        message = "오류가 발생 했습니다.",
                        actionLabel = "닫기",
                        duration = SnackbarDuration.Short
                    )
                }
            }

            DetailCalendarInfoUiState.Loading -> {
                LoadingDialog()
            }

            DetailCalendarInfoUiState.Success -> { }
        }

        DetailCalendarInfoScreen(date, time, kind, mp4Uri) {
            detailCalendarInfoViewModel.isIdle()
            snackBarHostState.currentSnackbarData?.dismiss()
            moveScreenWithArgs(
                navController,
                "${AppScreen.Calendar.route}/${Uri.encode(dateConvert(date))}"
            )
        }
    }
}

@Composable
private fun DetailCalendarInfoScreen(
    date: String,
    time : String,
    kind : String,
    mp4Uri: Uri,
    onBackBtnClick: () -> Unit) {
    Column {
        SubScreenHeader(pageName = "이상 행동 기록") {
            onBackBtnClick()
        }

        DateField(date)

        VideoField(mp4Uri)

        Text(
            text = "이상 행동 자료",
            modifier = Modifier
                .padding(top = 5.dp, bottom = 20.dp, end = 30.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            fontSize = 10.sp,
        )

        CustomDivider(horizontal = 0.dp, vertical = 0.dp)

        TimeField(time)
        KindField(kind)
    }
}

@Composable
private fun VideoField(mp4Uri: Uri) {
    when(mp4Uri) {
        Uri.EMPTY -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(start = 30.dp, end = 30.dp)
                    .background(Color.Black, RoundedCornerShape(10.dp))
            )
        } else -> {
            VideoPlayer(mp4Uri)
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun VideoPlayer(mp4Uri: Uri) {
    val context = LocalContext.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    val dataSourceFactory = DefaultDataSource.Factory(context)

    val mediaSource = remember(mp4Uri) {
        ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(
                MediaItem.fromUri(mp4Uri)
            )
    }

    LaunchedEffect(mediaSource) {
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = false
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(start = 30.dp, end = 30.dp)
            .clip(RoundedCornerShape(10.dp))
    )
}

@Composable
private fun DateField(date: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 20.dp)
            .height(IntrinsicSize.Min)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        var iconHeight by remember { mutableFloatStateOf(0f) }
        Icon(painter = painterResource(id = R.drawable.time),
            contentDescription = "time",
            modifier = Modifier
                .onGloballyPositioned {
                    iconHeight = it.size.height.toFloat()
                }
                .size(30.dp)
        )
        CustomVerticalDivider(iconHeight = iconHeight)

        Text(
            text = date,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxHeight()
                .wrapContentSize()
        )
    }
}

@Composable
private fun TimeField(time: String) {
    Row(
        modifier = Modifier
            .padding(start = 30.dp, top = 30.dp)
            .height(intrinsicSize = IntrinsicSize.Min)
    ) {
        var iconHeight by remember { mutableFloatStateOf(0f) }
        Icon(painter = painterResource(id = R.drawable.time2),
            contentDescription = "time",
            modifier = Modifier.onGloballyPositioned {
                iconHeight = it.size.height.toFloat()
            })
        CustomVerticalDivider(iconHeight = iconHeight)

        Text(
            text = time,
            modifier = Modifier
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun KindField(kind: String) {
    Row(
        modifier = Modifier
            .padding(start = 30.dp, top = 30.dp)
            .height(intrinsicSize = IntrinsicSize.Min)
    ) {
        var iconHeight by remember { mutableFloatStateOf(0f) }
        Icon(painter = painterResource(id = R.drawable.info),
            contentDescription = "kind",
            modifier = Modifier.onGloballyPositioned {
                iconHeight = it.size.height.toFloat()
            })
        CustomVerticalDivider(iconHeight = iconHeight)
        Text(
            text = kind,
            modifier = Modifier
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

private fun dateConvert(date: String): String {
    val koreanDateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    val isoDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val convertDate = LocalDate.parse(date, koreanDateFormatter)
    return convertDate.format(isoDateFormatter)
}


@Preview(showBackground = true)
@Composable
private fun DetailCalendarInfoScreenPreview() {
    DetailCalendarInfoScreen(
        "2024년 08월 14일",
        "14:30:23",
        "낙상",
        Uri.EMPTY
    ) { }
}