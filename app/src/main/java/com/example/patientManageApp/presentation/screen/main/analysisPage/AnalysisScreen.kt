package com.example.patientManageApp.presentation.screen.main.analysisPage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.patientManageApp.R
import com.example.patientManageApp.domain.entity.OccurrencesEntity
import com.example.patientManageApp.presentation.BackOnPressed
import com.example.patientManageApp.presentation.CustomDivider
import com.example.patientManageApp.presentation.CustomVerticalDivider
import com.example.patientManageApp.presentation.ScreenHeader
import com.example.patientManageApp.presentation.screen.main.MainViewModel
import com.example.patientManageApp.presentation.screen.main.calendarPage.MonthHeader
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
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.ceil

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AnalysisScreen(mainViewModel: MainViewModel) {
    BackOnPressed()

    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            mainViewModel.getUserData()
        },
    )

    var currentMonth by remember { mutableStateOf(LocalDate.now().yearMonth) }
    val previousMonth by remember(currentMonth) { mutableStateOf(currentMonth.minusMonths(1)) }

    val currentMonthOccurrenceCount = mainViewModel.getMonthlyOccurrenceCount(currentMonth)
    val previousMonthOccurrenceCount = mainViewModel.getMonthlyOccurrenceCount(previousMonth)
    val (maxOccurrenceMap, minOccurrenceMap) = mainViewModel.getMaxAndMinOccurrencesForMonth(currentMonth)
    val weeklyChartEntriesOfCurrentMonth = mainViewModel.getWeeklyChartEntries(currentMonth)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullToRefreshState)
    ) {
        AnalysisScreen(
            currentMonth = currentMonth,
            currentMonthOccurrenceCount = currentMonthOccurrenceCount,
            previousMonthOccurrenceCount = previousMonthOccurrenceCount,
            maxOccurrenceMap = maxOccurrenceMap,
            minOccurrenceMap = minOccurrenceMap,
            weeklyChartEntriesOfCurrentMonth = weeklyChartEntriesOfCurrentMonth,
            onLeftClick = {
                currentMonth = currentMonth.previousMonth
            },
            onRightClick = {
                currentMonth = currentMonth.nextMonth
            }
        )

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullToRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color.White,
            contentColor = Color.Black
        )
    }
}

@Composable
private fun AnalysisScreen(
    currentMonth: YearMonth,
    currentMonthOccurrenceCount: Int,
    previousMonthOccurrenceCount: Int,
    maxOccurrenceMap: Map<String, Int>,
    minOccurrenceMap: Map<String, Int>,
    weeklyChartEntriesOfCurrentMonth: List<ChartEntry>,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        ScreenHeader(pageName = "이상 행동 통계")

        MonthHeader(month = currentMonth,
            onLeftClick = { onLeftClick() },
            onRightClick = { onRightClick() })

        AnalysisChart(weeklyChartEntriesOfCurrentMonth = weeklyChartEntriesOfCurrentMonth)
        UnitField()
        CustomDivider(horizontal = 0.dp, vertical = 15.dp)
        SetAnalysisScreenInfo(
            R.drawable.warning,
            "이번 달 이상 행동 횟수",
            "${currentMonthOccurrenceCount}회"
        )
        SetAnalysisScreenInfo(
            R.drawable.arrow_up,
            "지난달 대비 증가 횟수",
            "${currentMonthOccurrenceCount - previousMonthOccurrenceCount}회"
        )

        if (maxOccurrenceMap.isNotEmpty()) {
            SetAnalysisScreenInfo(
                R.drawable.clock_loader_90,
                "최다 이상 행동 횟수",
                "${maxOccurrenceMap.keys.joinToString(", ")}: ${maxOccurrenceMap.values.max()}회"
            )
        }

        if (minOccurrenceMap.isNotEmpty() && maxOccurrenceMap.values.max() != minOccurrenceMap.values.min()) {
            SetAnalysisScreenInfo(
                R.drawable.clock_loader_10,
                "최소 이상 행동 횟수",
                "${minOccurrenceMap.keys.joinToString(", ")}: ${minOccurrenceMap.values.min()}회"
            )
        }
    }
}

@Composable
private fun AnalysisChart(weeklyChartEntriesOfCurrentMonth: List<ChartEntry>) {
    val chartEntryModelProducer = remember {
        ChartEntryModelProducer()
    }

    chartEntryModelProducer.setEntries(weeklyChartEntriesOfCurrentMonth)
    Chart(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .heightIn(320.dp, 350.dp),
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
            axisValuesOverrider = AxisValuesOverrider.fixed(minY = 0f, maxY = 30f)
        ),
        chartModelProducer = chartEntryModelProducer,
        startAxis = rememberStartAxis(
            itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 4)
        ),
        endAxis = rememberEndAxis(
            itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 4)
        ),
        topAxis = rememberTopAxis(
            valueFormatter = { value, _ ->
                val xAxisLabelData = listOf("1주", "2주", "3주", "4주", "5주")
                xAxisLabelData[value.toInt()]
            }
        ),
        bottomAxis = rememberBottomAxis(
            valueFormatter = { value, _ ->
                val xAxisLabelData = listOf("1주", "2주", "3주", "4주", "5주")
                xAxisLabelData[value.toInt()]
            }
        ),
        runInitialAnimation = true,
        isZoomEnabled = false
    )
}

@Composable
private fun UnitField() {
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
}

@Composable
private fun SetAnalysisScreenInfo(icon: Int, count: String, increment: String) {
    Row(
        modifier = Modifier
            .padding(start = 20.dp, top = 10.dp)
    ) {
        var iconHeight by remember { mutableFloatStateOf(0f) }
        Icon(painter = painterResource(id = icon),
            contentDescription = "infoIcon",
            modifier = Modifier.onGloballyPositioned {
                iconHeight = it.size.height.toFloat()
            })
        CustomVerticalDivider(iconHeight = iconHeight)
        Column(modifier = Modifier.padding(start = 15.dp, bottom = 10.dp)) {
            Text(text = count, fontSize = 15.sp)
            Text(text = increment, fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.5.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalysisScreenPreview() {
    val occurrenceDays = hashMapOf(
        "2024-08-14" to listOf(
            OccurrencesEntity("09:00:24", "낙상"),
            OccurrencesEntity("23:00:23", "낙상"),
            OccurrencesEntity("23:00:53", "화재"),
            OccurrencesEntity("23:00:54", "화재")
        ),
        "2024-08-24" to listOf(
            OccurrencesEntity("11:00:24", "낙상"),
            OccurrencesEntity("21:00:53", "낙상")
        ),
        "2024-07-15" to listOf(
            OccurrencesEntity("09:00:34", "낙상")
        )
    )

    val groupByMonth = occurrenceDays.entries.groupBy {
        val date = LocalDate.parse(it.key)
        "${date.year}-${date.monthValue.toString().padStart(2, '0')}"
    }

    val groupByWeekOfMonth = occurrenceDays.entries.groupBy {
        val date = LocalDate.parse(it.key)
        val weekOfMonth = ceil(date.dayOfMonth.toDouble() / 7).toInt()
        "${date.year}-${date.monthValue.toString().padStart(2, '0')}-$weekOfMonth"
    }

    var currentMonth by remember { mutableStateOf(LocalDate.now().yearMonth) }
    val previousMonth by remember(currentMonth) { mutableStateOf(currentMonth.minusMonths(1)) }

    fun getOccurrenceCount(occurrenceList: List<MutableMap.MutableEntry<String, List<OccurrencesEntity>>>): Int {
        var count = 0
        occurrenceList.forEach {
            count += it.value.size
        }
        return count
    }

    fun getMaxAndMinOccurrenceCount(occurrenceList: List<MutableMap.MutableEntry<String, List<OccurrencesEntity>>>): Pair<Map<String, Int>, Map<String, Int>> {
        val occurrenceMap = hashMapOf<String, Int>()
        occurrenceList.forEach {
            it.value.forEach { occurrence ->
                if (!occurrenceMap.containsKey(occurrence.kind)) {
                    occurrenceMap[occurrence.kind] = 0
                }
                occurrenceMap[occurrence.kind] = (occurrenceMap[occurrence.kind])!! + 1
            }
        }

        if (occurrenceMap.isNotEmpty()) {
            val minValue = occurrenceMap.values.min()
            val maxValue = occurrenceMap.values.max()

            val maxEntries = occurrenceMap.filter { it.value == maxValue }
            val minEntries = occurrenceMap.filter { it.value == minValue }

            return Pair(maxEntries, minEntries)
        } else {
            return Pair(mapOf(), mapOf())
        }
    }

    fun getWeeklyChartEntries(month: YearMonth): List<ChartEntry> {
        val groupByWeekOfMonth = groupByWeekOfMonth

        return (1..5).map { week ->
            val weekKey = "$month-$week"
            val occurrenceCount = getOccurrenceCount(groupByWeekOfMonth[weekKey] ?: emptyList())

            entryOf(
                x = (week - 1).toFloat(),
                y = occurrenceCount.toFloat()
            )
        }
    }

    AnalysisScreen(
        currentMonth = currentMonth,
        currentMonthOccurrenceCount = getOccurrenceCount(
            groupByMonth[currentMonth.toString()] ?: emptyList()
        ),
        previousMonthOccurrenceCount = getOccurrenceCount(
            groupByMonth[previousMonth.toString()] ?: emptyList()
        ),
        maxOccurrenceMap = getMaxAndMinOccurrenceCount(groupByMonth[currentMonth.toString()] ?: emptyList()).first,
        minOccurrenceMap = getMaxAndMinOccurrenceCount(groupByMonth[currentMonth.toString()] ?: emptyList()).second,
        weeklyChartEntriesOfCurrentMonth = getWeeklyChartEntries(currentMonth),
        onLeftClick = {
            currentMonth = currentMonth.previousMonth
        },
        onRightClick = {
            currentMonth = currentMonth.nextMonth
        }
    )
}

