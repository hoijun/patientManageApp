package com.example.patientManageApp.presentation.screen.main

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.patientManageApp.domain.entity.CameraEntity
import com.example.patientManageApp.domain.entity.OccurrencesEntity
import com.example.patientManageApp.domain.entity.PatientEntity
import com.example.patientManageApp.domain.entity.UserEntity
import com.example.patientManageApp.domain.usecase.UseCases
import com.example.patientManageApp.domain.utils.getResult
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class MainViewModel@Inject constructor(private val useCases: UseCases): ViewModel() {
    private val _mainUiState = MutableStateFlow<MainUiState>(MainUiState.Idle)
    val mainUiState = _mainUiState.asStateFlow()

    private val _userData = MutableStateFlow(UserEntity("", ""))
    val userData = _userData.asStateFlow()

    private val _patientData = MutableStateFlow(PatientEntity("", ""))
    val patientData = _patientData.asStateFlow()

    private val _cameraData = MutableStateFlow<List<CameraEntity>>(emptyList())
    val cameraData = _cameraData.asStateFlow()

    var occurrenceData = HashMap<String, List<OccurrencesEntity>>()

    fun getUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading()
            val userResult = CompletableDeferred<Boolean>()
            val patientResult = CompletableDeferred<Boolean>()
            val cameraResult = CompletableDeferred<Boolean>()
            val occurrenceResult = CompletableDeferred<Boolean>()

            useCases.getUserData().getResult(
                success = {
                    _userData.value = it.data
                    userResult.complete(true)
                },
                error = {
                    _userData.value = UserEntity("", "")
                    userResult.complete(false)
                }
            )

            useCases.getPatientData().getResult(
                success = {
                    _patientData.value = it.data
                    patientResult.complete(true)
                },
                error = {
                    _patientData.value = PatientEntity("", "")
                    patientResult.complete(false)
                }
            )

            useCases.getCameraData().getResult(
                success = {
                    _cameraData.value = it.data
                    cameraResult.complete(true)
                },
                error = {
                    _cameraData.value = emptyList()
                    cameraResult.complete(false)
                }
            )

            useCases.getOccurrenceData().getResult(
                success = {
                    occurrenceData = it.data
                    occurrenceResult.complete(true)
                },
                error = {
                    occurrenceData = hashMapOf()
                    occurrenceResult.complete(false)
                }
            )

            if (!userResult.await() && !patientResult.await() && !cameraResult.await() && !occurrenceResult.await()) {
                isError()
            } else {
                isSuccess()
            }

            setOccurrencesGroupedByMonth()
            setOccurrencesGroupedByWeekOfMonth()
        }
    }

    fun updateUserData(userEntity: UserEntity) {
        _userData.value = userEntity
    }

    fun updatePatientData(patientEntity: PatientEntity) {
        _patientData.value = patientEntity
    }

    fun updateCameraData(cameraEntities: List<CameraEntity>) {
        _cameraData.value = cameraEntities
    }

    private var occurrencesGroupedByMonth: Map<String, List<Map.Entry<String, List<OccurrencesEntity>>>> = mapOf()
    private var occurrencesGroupedByWeekOfMonth: Map<String, List<Map.Entry<String, List<OccurrencesEntity>>>> = mapOf()

    private fun setOccurrencesGroupedByMonth() {
        occurrencesGroupedByMonth = occurrenceData.entries.groupBy {
            val date = LocalDate.parse(it.key)
            "${date.year}-${date.monthValue.toString().padStart(2, '0')}"
        }
    }

    private fun setOccurrencesGroupedByWeekOfMonth() {
        occurrencesGroupedByWeekOfMonth = occurrenceData.entries.groupBy {
            val date = LocalDate.parse(it.key)
            val weekOfMonth = ceil(date.dayOfMonth.toDouble() / 7).toInt()
            "${date.year}-${date.monthValue.toString().padStart(2, '0')}-$weekOfMonth"
        }
    }

    fun getMonthlyOccurrenceCount(month: YearMonth): Int {
        val groupByMonth = occurrencesGroupedByMonth
        return getOccurrenceCount(groupByMonth[month.toString()] ?: emptyList())
    }

    private fun getOccurrenceCount(occurrenceList: List<Map.Entry<String, List<OccurrencesEntity>>>): Int {
        var count = 0
        occurrenceList.forEach {
            count += it.value.size
        }
        return count
    }

    fun getMaxAndMinOccurrencesForMonth(month: YearMonth): Pair<Map<String, Int>, Map<String, Int>> {
        val groupByMonth = occurrencesGroupedByMonth
        return getMaxAndMinOccurrenceCount(groupByMonth[month.toString()] ?: emptyList())
    }

    private fun getMaxAndMinOccurrenceCount(occurrenceList: List<Map.Entry<String, List<OccurrencesEntity>>>): Pair<Map<String, Int>, Map<String, Int>> {
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
        val groupByWeekOfMonth = occurrencesGroupedByWeekOfMonth

        return (1..5).map { week ->
            val weekKey = "$month-$week"
            val occurrenceCount = getOccurrenceCount(groupByWeekOfMonth[weekKey] ?: emptyList())

            entryOf(
                x = (week - 1).toFloat(),
                y = occurrenceCount.toFloat()
            )
        }
    }

    private fun isLoading() {
        _mainUiState.value = MainUiState.Loading
    }

    private fun isError() {
        _mainUiState.value = MainUiState.Error
    }

    private fun isSuccess() {
        _mainUiState.value = MainUiState.Success
    }

    fun isIdle() {
        _mainUiState.value = MainUiState.Idle
    }
}