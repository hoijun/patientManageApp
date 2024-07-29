package com.example.patientManageApp.presentation.screen.calendarPage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.patientManageApp.R
import com.example.patientManageApp.presentation.noRippleClickable
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.yearMonth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun Day(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .border(
                if (isSelected) 2.dp else 0.dp,
                if (isSelected) Color.Gray else Color.Transparent,
                RoundedCornerShape(7.5.dp)
            )
            .noRippleClickable(enabled = day.position == DayPosition.MonthDate) {
                onClick(day)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .padding(top = 4.5.dp)
                .align(Alignment.TopCenter),
            text = day.date.dayOfMonth.toString(),
            color = if (day.position == DayPosition.MonthDate) {
                when (day.date.dayOfWeek) {
                    DayOfWeek.SUNDAY -> Color.Red
                    DayOfWeek.SATURDAY -> Color.Blue
                    else -> Color.Black
                }
            } else Color.Gray,
            fontSize = 11.sp
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            val path = Path().apply {
                moveTo(15f, 0f)
                lineTo(size.width - 15f, 0f)
            }

            if (day.date.dayOfMonth == 10) {
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
    }
}

@Composable
fun DaysOfWeekTitle(modifier: Modifier, daysOfWeek: List<DayOfWeek>) {
    Row(modifier = modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                color = if (dayOfWeek.value == 6) Color.Blue else if (dayOfWeek.value == 7) Color.Red else Color.Black
            )
        }
    }
}

@Composable
fun MonthHeader(month: YearMonth, onLeftClick: () -> Unit, onRightClick: () -> Unit) {
    val minMonth = YearMonth.of(2023, 1)
    val maxMonth = LocalDate.now().yearMonth
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min)
            .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painter = painterResource(id = R.drawable.arrow_left),
            contentDescription = null,
            modifier = Modifier.noRippleClickable(enabled = month > minMonth) {
                onLeftClick()
            },
            tint = if (month > minMonth) Color.Black else Color.LightGray
        )
        Text(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(align = Alignment.CenterVertically),
            text = "${month.year}년 ${month.month.value}월",
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Icon(
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = null,
            modifier = Modifier.noRippleClickable(enabled = month < maxMonth) {
                onRightClick()
            },
            tint = if (month < maxMonth) Color.Black else Color.LightGray
        )
    }
}
