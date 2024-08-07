package com.example.patientManageApp.presentation.screen.login.loginPage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.patientManageApp.R
import com.example.patientManageApp.presentation.BackOnPressed
import com.example.patientManageApp.presentation.ScreenHeader
import com.example.patientManageApp.presentation.noRippleClickable

@Composable
fun TermOfServiceScreen(movePage: () -> Unit) {
    var isCheckedFirst by remember { mutableStateOf(false) }
    var isCheckedSecond by remember { mutableStateOf(false) }
    val isSubmitEnabled by remember {
        derivedStateOf { isCheckedFirst && isCheckedSecond }
    }
    val allCheck = isCheckedFirst && isCheckedSecond

    BackHandler { }

    Column {
        ScreenHeader(pageName = "약관 동의")
        TermsCheckbox(
            modifier = Modifier.padding(top = 30.dp),
            text = "모두 동의",
            isChecked = allCheck
        ) {
            if (allCheck) {
                isCheckedFirst = false
                isCheckedSecond = false
            } else {
                isCheckedFirst = true
                isCheckedSecond = true
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            thickness = 1.dp,
            color = Color(0xFFc0c2c4)
        )
        TermsCheckbox(
            Modifier,
            text = "이용 약관",
            isChecked = isCheckedFirst
        ) {
            isCheckedFirst = !isCheckedFirst
        }
        TermsCheckbox(
            modifier = Modifier.padding(top = 20.dp),
            text = "개인정보처리방침",
            isChecked = isCheckedSecond
        ) {
            isCheckedSecond = !isCheckedSecond
        }
        Spacer(modifier = Modifier.weight(1f))
        SubmitButton(enabled = isSubmitEnabled) {
            movePage()
        }
    }
}

@Composable
private fun TermsCheckbox(
    modifier: Modifier,
    text: String, isChecked: Boolean,
    onCheckedChange: () -> Unit)
{
    Row(
        modifier = modifier
            .padding(start = 25.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .size(22.dp)
                .noRippleClickable {
                    onCheckedChange()
                },
            shape = RoundedCornerShape(5.dp),
            colors = CardDefaults.cardColors(Color.Transparent),
            border = BorderStroke(1.dp, Color.LightGray),
            elevation = CardDefaults.cardElevation(0.dp),
        ) {
            if (isChecked) {
                Icon(
                    painter = painterResource(id = R.drawable.check),
                    contentDescription = "check",
                    modifier = Modifier.padding(1.dp),
                )
            }
        }
        Text(text = text,
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 10.dp))
        Spacer(modifier = Modifier.weight(1f))
        if (text != "모두 동의") {
            Text(text = "보기",
                fontSize = 12.sp,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                modifier = Modifier
                    .padding(end = 30.dp)
                    .fillMaxHeight()
                    .wrapContentHeight(align = Alignment.Bottom)
                    .noRippleClickable {
                    }
            )
        }
    }
}

@Composable
private fun SubmitButton(enabled: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Button(
        onClick = { onClick() },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFc0c2c4),
            contentColor = Color.Black,
            disabledContainerColor = Color(0xFFc0c2c4).copy(alpha = 0.5f),
            disabledContentColor = Color.Black.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .padding(start = 30.dp, end = 30.dp, bottom = 20.dp)
            .indication(
                interactionSource = interactionSource,
                rememberRipple(color = Color(0xfff1f3f5))
            )
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        interactionSource = interactionSource
    ) {
        Text("다음으로")
    }
}

@Preview(showBackground = true)
@Composable
fun TermOfServiceScreenPreview() {
    TermOfServiceScreen {

    }
}