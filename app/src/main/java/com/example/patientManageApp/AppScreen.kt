package com.example.patientManageApp

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class AppScreen(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int = 0
) {
    data object Home: AppScreen("home", R.string.home, R.drawable.webcam)
    data object SettingCamera: AppScreen("settingCamera", R.string.settingCamera)
    data object Calendar: AppScreen("calendar", R.string.calendar, R.drawable.calendar)
    data object DetailCalendarInfo: AppScreen("detailCalendarInfo", R.string.detail_CalendarInfo)
    data object Analysis: AppScreen("analysis", R.string.analysis, R.drawable.analytic)
    data object MyPage: AppScreen("myPage", R.string.myPage, R.drawable.account)
    data object UserProfile: AppScreen("userProfile", R.string.userProfile)
    data object PatientProfile: AppScreen("patientProfile", R.string.patientProfile)
}

sealed class LoginAppScreen(
    val route: String,
    @StringRes val title: Int
) {
    data object Login: LoginAppScreen("login", R.string.login)
    data object UserProfile: LoginAppScreen("userProfile", R.string.userProfile)
    data object PatientProfile: LoginAppScreen("patientProfile", R.string.patientProfile)
}