package com.example.patientManageApp

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class AppScreen(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int = 0
) {
    data object Home: AppScreen("home", R.string.home, R.drawable.webcam)
    data object Calendar: AppScreen("calendar", R.string.calendar, R.drawable.calendar)
    data object Analysis: AppScreen("analysis", R.string.analysis, R.drawable.analytic)
    data object MyPage: AppScreen("mypage", R.string.mypage, R.drawable.account)
}