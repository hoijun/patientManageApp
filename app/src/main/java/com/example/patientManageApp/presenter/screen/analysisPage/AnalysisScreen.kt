package com.example.patientManageApp.presenter.screen.analysisPage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.patientManageApp.presenter.BackOnPressed
import com.example.patientManageApp.R
import com.example.patientManageApp.presenter.ScreenHeader
import com.example.patientManageApp.presenter.screen.calendarPage.MonthHeader
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.core.yearMonth
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberTopAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberEndAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import java.time.LocalDate

@Composable
fun AnalysisScreen() {
    BackOnPressed()

    var currentMonth by remember { mutableStateOf(LocalDate.now().yearMonth) }
    Column {
        ScreenHeader(pageName = "이상 행동 통계")

        MonthHeader(month = currentMonth,
            onLeftClick = { currentMonth = currentMonth.previousMonth },
            onRightClick = { currentMonth = currentMonth.nextMonth })

        Chart(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .fillMaxHeight(0.5f),
            chart = columnChart(
                columns = listOf(
                    lineComponent(
                        color = Color.Gray,
                        thickness = 10.dp,
                        shape = Shapes.roundedCornerShape(topLeftPercent = 30, topRightPercent = 30)
                    )
                ),
                dataLabel = TextComponent.Builder().build().apply {
                    margins.set(2f)
                    color = Color.Black.toArgb()
                },
                axisValuesOverrider = AxisValuesOverrider.fixed(minY = 0f, maxY = 15f)
            ),
            chartModelProducer = ChartEntryModelProducer(
                listOf(
                    entryOf(x = 0f, y = 4f),
                    entryOf(x = 1f, y = 5f),
                    entryOf(x = 2f, y = 9f),
                    entryOf(x = 3f, y = 4f)
                )
            ),
            startAxis = rememberStartAxis(
                itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 4)
            ),
            endAxis = rememberEndAxis(
                itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 4)
            ),
            topAxis = rememberTopAxis(
                valueFormatter = { value, _ ->
                    val xAxisLabelData = listOf("1주", "2주", "3주", "4주")
                    (xAxisLabelData[value.toInt()])
                }
            ),
            bottomAxis = rememberBottomAxis(
                valueFormatter = { value, _ ->
                    val xAxisLabelData = listOf("1주", "2주", "3주", "4주")
                    (xAxisLabelData[value.toInt()])
                }
            ),
            runInitialAnimation = false
        )

        Row(modifier = Modifier.padding(start = 45.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(20.dp)
            ) {
                Canvas(
                    modifier = Modifier.size(12.dp)
                ) {
                    drawCircle(
                        color = Color.Gray,
                        radius = 6.dp.toPx(),
                        center = Offset(6.dp.toPx(), 6.dp.toPx())
                    )
                }
                Text(
                    text = "이상 행동 횟수(회)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
        ) {
            drawLine(
                Color(0xFFc0c2c4),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        }

        SetAnalysisScreenInfo(R.drawable.warning, "이번 달 이상 행동 횟수", "22회")
        SetAnalysisScreenInfo(R.drawable.arrow_up, "지난달 대비 증가 횟수", "4회")
    }
}

@Composable
private fun SetAnalysisScreenInfo(icon: Int, text: String, text2: String) {
    Row(
        modifier = Modifier
            .padding(start = 20.dp, top = 10.dp)
    ) {
        var iconHeight = 0f
        Icon(painter = painterResource(id = icon),
            contentDescription = "arrow_up",
            modifier = Modifier.onGloballyPositioned {
                iconHeight = it.size.height.toFloat()
            })
        Canvas(
            modifier = Modifier
                .padding(start = 20.dp)
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
        Column(modifier = Modifier.padding(start = 15.dp, bottom = 10.dp)) {
            Text(text = text, fontSize = 15.sp)
            Text(text = text2, fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.5.dp))
        }
    }
}