package com.example.patientManageApp.domain.utils

import android.util.Log

sealed class FirebaseApiResult<T> {
    data class Success<T>(val data: T) : FirebaseApiResult<T>()
    data class Error<T>(val error: Exception) : FirebaseApiResult<T>()
}

inline fun <T, R> FirebaseApiResult<T>.getResult(
    success: (FirebaseApiResult.Success<T>) -> R,
    error: (FirebaseApiResult.Error<T>) -> R
): R = when (this) {
    is FirebaseApiResult.Success -> success(this)
    is FirebaseApiResult.Error -> error(this)
}

inline fun <T> FirebaseApiResult<T>.onSuccess(
    action: (T) -> Unit
): FirebaseApiResult<T> {
    if (this is FirebaseApiResult.Success) action(data)
    return this
}

inline fun <T> FirebaseApiResult<T>.onError(
    action: (Throwable) -> Unit
): FirebaseApiResult<T> {
    if (this is FirebaseApiResult.Error) action(error)
    return this
}