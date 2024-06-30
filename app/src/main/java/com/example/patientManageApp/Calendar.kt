package com.example.patientManageApp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

class Calendar {
    companion object {
        @Composable
        fun Day(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(5.dp)
                    .clip(RoundedCornerShape(7.5.dp))
                    .border(if(isSelected) 2.dp else 0.dp
                        ,if(isSelected) Color.Gray else Color.Transparent, RoundedCornerShape(7.5.dp))
                    .clickable(enabled = day.position == DayPosition.MonthDate,
                        onClick = { onClick(day) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        modifier = Modifier.padding(vertical = 6.dp),
                        text = day.date.dayOfMonth.toString(),
                        color = if (day.position == DayPosition.MonthDate) {
                            when (day.date.dayOfWeek) {
                                DayOfWeek.SUNDAY -> Color.Red
                                DayOfWeek.SATURDAY -> Color.Blue
                                else -> Color.Black
                            }
                        } else Color.Gray,
                        fontSize = 12.sp
                    )
                    Canvas(modifier = Modifier.fillMaxWidth()) {
                        val border = if (isSelected) 2f else 0f
                        if (day.date.dayOfMonth == 10) {
                            drawLine(
                                Color.Black,
                                start = Offset(0f + border, 0f),
                                end = Offset(size.width - border, 0f),
                                strokeWidth = 1.5.dp.toPx()
                            )
                        }
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
        fun MonthHeader(month: CalendarMonth, onLeftClick: () -> Unit, onRightClick: () -> Unit) {
            Row(modifier = Modifier.fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min)
                .padding(horizontal = 15.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = onLeftClick ) {
                    Icon(painter = painterResource(id = R.drawable.arrow_left),
                        contentDescription = null,
                        modifier = Modifier.fillMaxHeight())
                }
                Text(
                    modifier = Modifier.fillMaxHeight()
                        .wrapContentHeight(align = Alignment.CenterVertically),
                    text = "${month.yearMonth.year}년 ${month.yearMonth.month.value}월",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onRightClick ) {
                    Icon(painter = painterResource(id = R.drawable.arrow_right),
                        contentDescription = null,
                        modifier = Modifier.fillMaxHeight())
                }
            }
        }
    }
}