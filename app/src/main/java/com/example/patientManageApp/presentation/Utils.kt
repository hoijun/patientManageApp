package com.example.patientManageApp.presentation

import android.app.Activity
import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.example.patientManageApp.R
import java.time.LocalDate

@Composable
fun ScreenHeader(pageName: String) {
    Column {
        Text(
            text = pageName,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 15.dp)
                .fillMaxWidth()
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            drawLine(
                Color(0xFFc0c2c4),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

@Composable
fun SubScreenHeader(pageName: String, onBackBtnClick: () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "arrow_back",
                modifier = Modifier
                    .padding(start = 20.dp)
                    .noRippleClickable {
                        onBackBtnClick()
                    })
            Text(
                text = pageName,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(vertical = 20.dp, horizontal = 5.dp)
            )
        }
        CustomDivider(horizontal = 0.dp, vertical = 0.dp)
    }
}

@Composable
fun BackOnPressed() {
    val context = LocalContext.current
    var backPressedState by remember { mutableStateOf(true) }
    var backPressedTime = 0L

    BackHandler(enabled = backPressedState) {
        if (System.currentTimeMillis() - backPressedTime < 2500) {
            // 앱 종료
            (context as Activity).finish()
        } else {
            backPressedState = true
            Toast.makeText(context, "한 번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WheelDatePicker(
    width: Dp,
    itemHeight: Dp,
    items: List<Int>,
    initialItem: Int,
    unit: String,
    onItemSelected: (index: Int, item: Int) -> Unit = { _, _ -> }
) {
    val itemHalfHeight = LocalDensity.current.run { itemHeight.toPx() / 2f }
    val scrollState = rememberLazyListState(0)
    var lastSelectedIndex by remember {
        mutableIntStateOf(0)
    }

    var itemsState by remember {
        mutableStateOf(items)
    }

    LaunchedEffect(items) {
        var targetIndex = items.indexOf(initialItem) - 2
        targetIndex += ((Int.MAX_VALUE / 2) / items.size) * items.size
        itemsState = items
        lastSelectedIndex = targetIndex
        scrollState.scrollToItem(targetIndex)
    }

    LazyColumn(
        modifier = Modifier
            .width(width)
            .height(itemHeight * 5),
        state = scrollState,
        flingBehavior = rememberSnapFlingBehavior(
            lazyListState = scrollState
        )
    ) {
        items(count = Int.MAX_VALUE,
            itemContent = { i ->
                val item = items[i % itemsState.size]

                Box(modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        val y = coordinates.positionInParent().y + itemHalfHeight
                        val parentHalfHeight = itemHalfHeight * 5
                        val isSelected =
                            y in (parentHalfHeight - itemHalfHeight * 1.5f)..(parentHalfHeight + itemHalfHeight * 1.5f)
                        if (isSelected && lastSelectedIndex != i) {
                            onItemSelected(i % itemsState.size, item)
                            lastSelectedIndex = i
                        }
                    }
                ) {
                    Text(
                        text = "${item}${unit}",
                        color = if (lastSelectedIndex == i) {
                            Color.Black
                        } else {
                            Color.Gray
                        },
                        fontSize = if (lastSelectedIndex == i) {
                            20.sp
                        } else {
                            17.sp
                        },
                        fontWeight = if (lastSelectedIndex == i) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateBottomSheet(modifier: Modifier, closeSheet: (birth: String) -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    var year by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = { closeSheet("${year}년 ${month}월 ${date}일") },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        containerColor = Color.White,
        dragHandle = null
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 40.dp)
        ) {
            WheelDatePicker(
                width = 100.dp,
                itemHeight = 50.dp,
                items = (1900..LocalDate.now().year).toList(),
                initialItem = LocalDate.now().year,
                "년",
                onItemSelected = { _, item ->
                    year = item.toString()
                }
            )

            WheelDatePicker(
                width = 100.dp,
                itemHeight = 50.dp,
                items = (1..12).toList(),
                initialItem = LocalDate.now().monthValue,
                "월",
                onItemSelected = { _, item ->
                    month = item.toString()
                }
            )

            WheelDatePicker(
                width = 100.dp,
                itemHeight = 50.dp,
                items = (1..LocalDate.now().month.maxLength()).toList(),
                initialItem = LocalDate.now().dayOfMonth,
                "일",
                onItemSelected = { _, item ->
                    date = item.toString()
                }
            )
        }
    }
}

@Composable
fun LoadingDialog() {
    BackHandler(enabled = true) { }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.5f))
            .zIndex(Float.MAX_VALUE)
            .noRippleClickable(false) { },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.Black,
            strokeWidth = 6.dp,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun Modifier.innerShadow(
    shape: Shape,
    color: Color = Color.Black,
    blur: Dp = 4.dp,
    offsetY: Dp = 2.dp,
    offsetX: Dp = 2.dp
) = this.drawWithContent {
    drawContent()
    drawIntoCanvas { canvas ->
        val shadowSize = Size(size.width, size.height)
        val shadowOutline = shape.createOutline(shadowSize, layoutDirection, this)
        val paint = Paint()
        paint.color = color
        canvas.saveLayer(size.toRect(), paint)
        canvas.drawOutline(shadowOutline, paint)

        paint.asFrameworkPaint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            if (blur.toPx() > 0) {
                maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
            }
        }

        paint.color = Color.Black
        canvas.translate(offsetX.toPx(), offsetY.toPx())
        canvas.drawOutline(shadowOutline, paint)
        canvas.restore()
    }
}

@Composable
fun CustomDivider(horizontal: Dp, vertical: Dp) {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontal, vertical = vertical),
        thickness = 1.dp,
        color = Color(0xFFc0c2c4)
    )
}

@Composable
fun WarningDialog(
    title: String,
    description: String,
    onDismissRequest: () -> Unit,
    onClickConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                text = description,
                fontSize = 15.sp,
            )
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(
                    onClick = { onClickConfirm() }
                ) {
                    Text("확인")
                }
                TextButton(
                    onClick = { onDismissRequest() }
                ) {
                    Text("취소")
                }
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        shape = RoundedCornerShape(10.dp),
    )
}

@Composable
fun CustomVerticalDivider(iconHeight: Float) {
    Canvas(
        modifier = Modifier
            .padding(start = 10.dp)
    ) {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, iconHeight)
        }
        drawPath(
            path = path,
            color = Color.Black,
            style = Stroke(
                3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}

@Composable
fun WithdrawalWarningDialog(
    title: String,
    description: String,
    onDismissRequest: () -> Unit,
    onClickConfirm: () -> Unit
) {
    val charSet = ('a'..'z') + ('0'..'9')
    val randomString = List(8) { charSet.random() }.joinToString("")
    var input by remember { mutableStateOf("") }
    var isInputCorrect by remember { mutableStateOf(true) }
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column {
                Text(
                    text = description,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 15.dp)
                )

                Text(
                    text = randomString,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.LightGray.copy(0.2f),
                            RoundedCornerShape(5.dp))
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    modifier = Modifier.padding(top = 10.dp),
                    singleLine = true,
                    maxLines = 1,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.DarkGray,
                        unfocusedBorderColor = Color.DarkGray,
                        disabledBorderColor = Color.LightGray,
                        disabledTextColor = Color.LightGray
                    )
                )

                if(!isInputCorrect) {
                    Text(
                        text = "입력이 틀렸습니다.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(
                    onClick = {
                        if (input == randomString) {
                            isInputCorrect = true
                            onClickConfirm()
                        } else {
                            isInputCorrect = false
                        }
                    }
                ) {
                    Text("확인", color = Color.DarkGray)
                }
                TextButton(
                    onClick = { onDismissRequest() }
                ) {
                    Text("취소", color = Color.DarkGray)
                }
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        shape = RoundedCornerShape(10.dp),
    )
}

@Composable
fun ShadowDivider() {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(15.dp)
        .innerShadow(
            RectangleShape,
            color = Color.Black.copy(0.3f),
            offsetY = (-2).dp,
            offsetX = (-2).dp
        )
        .innerShadow(
            RectangleShape,
            color = Color.Black.copy(0.3f),
            offsetY = 2.dp,
            offsetX = 2.dp
        ),
        color = Color(0xFFc0c2c4)
    ) { }
}

@Composable
fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
) = this.clickable(
    enabled = enabled,
    indication = null,
    interactionSource = remember {
        MutableInteractionSource()
    }
) {
    onClick()
}


fun moveScreen(navController: NavHostController, route: String) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let {
            popUpTo(it) { saveState = true }
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun moveScreenWithArgs(navController: NavHostController, route: String) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let {
            popUpTo(it) { saveState = true }
        }
        launchSingleTop = true
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WithdrawalWarningDialog(
        title = "title",
        description = "description",
        onDismissRequest = {},
        onClickConfirm = {}
    )
}