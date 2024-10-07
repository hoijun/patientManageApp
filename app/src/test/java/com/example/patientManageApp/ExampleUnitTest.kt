package com.example.patientManageApp

import com.example.patientManageApp.domain.entity.CameraEntity
import com.example.patientManageApp.domain.entity.OccurrencesEntity
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val occurrenceList = listOf(
            mutableMapOf("Entry1" to listOf(
                OccurrencesEntity("2024-03-01 10:00", "A"),
                OccurrencesEntity("2024-03-01 11:00", "B"),
                OccurrencesEntity("2024-03-01 12:00", "A")
            )).entries.first(),
            mutableMapOf("Entry2" to listOf(
                OccurrencesEntity("2024-03-02 10:00", "B"),
                OccurrencesEntity("2024-03-02 11:00", "C"),
                OccurrencesEntity("2024-03-02 12:00", "C")
            )).entries.first()
        )

        val result = getMaxAndMinOccurrenceCount(occurrenceList)

        // 최대 발생 횟수 테스트
        assertEquals(3, result.first.size)
        assertEquals(2, result.first["A"])
        assertEquals(2, result.first["B"])
        assertEquals(2, result.first["C"])

        // 최소 발생 횟수 테스트
        assertEquals(3, result.second.size)
        assertEquals(2, result.second["A"])
        assertEquals(2, result.second["B"])
        assertEquals(2, result.second["C"])
    }

    @Test
    fun testEmptyList() {
        val emptyList = listOf<MutableMap.MutableEntry<String, List<OccurrencesEntity>>>()
        val result = getMaxAndMinOccurrenceCount(emptyList)

        assertEquals(0, result.first.size)
        assertEquals(0, result.second.size)
    }

    @Test
    fun testSingleOccurrence() {
        val singleOccurrenceList = listOf(
            mutableMapOf("Entry" to listOf(OccurrencesEntity("2024-03-01 10:00", "A"))).entries.first()
        )

        val result = getMaxAndMinOccurrenceCount(singleOccurrenceList)

        assertEquals(1, result.first.size)
        assertEquals(1, result.first["A"])
        assertEquals(1, result.second.size)
        assertEquals(1, result.second["A"])
    }

    @Test
    fun testMultipleMaxAndMin() {
        val multipleMaxMinList = listOf(
            mutableMapOf("Entry1" to listOf(
                OccurrencesEntity("2024-03-01 10:00", "A"),
                OccurrencesEntity("2024-03-01 11:00", "B"),
                OccurrencesEntity("2024-03-01 12:00", "A"),
                OccurrencesEntity("2024-03-01 13:00", "C")
            )).entries.first(),
            mutableMapOf("Entry2" to listOf(
                OccurrencesEntity("2024-03-02 10:00", "B"),
                OccurrencesEntity("2024-03-02 11:00", "D"),
                OccurrencesEntity("2024-03-02 12:00", "C")
            )).entries.first()
        )

        val result = getMaxAndMinOccurrenceCount(multipleMaxMinList)

        // 최대 발생 횟수 테스트
        assertEquals(3, result.first.size)
        assertEquals(2, result.first["A"])
        assertEquals(2, result.first["C"])
        assertEquals(2, result.first["B"])

        // 최소 발생 횟수 테스트
        assertEquals(1, result.second.size)
        assertEquals(1, result.second["D"])
    }

    @Test
    fun testGetOccurrenceCount() {
        val occurrenceList = listOf(
            mutableMapOf("Entry1" to listOf(
                OccurrencesEntity("2024-03-01 10:00", "A"),
                OccurrencesEntity("2024-03-01 11:00", "B"),
                OccurrencesEntity("2024-03-01 12:00", "A")
            )).entries.first(),
            mutableMapOf("Entry2" to listOf(
                OccurrencesEntity("2024-03-02 10:00", "B"),
                OccurrencesEntity("2024-03-02 11:00", "C")
            )).entries.first()
        )

        val count = getOccurrenceCount(occurrenceList)
        assertEquals(5, count)
    }

    @Test
    fun testGetOccurrenceCountEmptyList() {
        val emptyList = listOf<MutableMap.MutableEntry<String, List<OccurrencesEntity>>>()
        val count = getOccurrenceCount(emptyList)
        assertEquals(0, count)
    }

    @Test
    fun testGetOccurrenceCountSingleEntry() {
        val singleEntryList = listOf(
            mutableMapOf("Entry" to listOf(
                OccurrencesEntity("2024-03-01 10:00", "A")
            )).entries.first()
        )

        val count = getOccurrenceCount(singleEntryList)
        assertEquals(1, count)
    }

    @Test
    fun testGetOccurrenceCountMultipleEntries() {
        val multipleEntriesList = listOf(
            mutableMapOf("Entry1" to listOf(
                OccurrencesEntity("2024-03-01 10:00", "A"),
                OccurrencesEntity("2024-03-01 11:00", "B")
            )).entries.first(),
            mutableMapOf("Entry2" to listOf(
                OccurrencesEntity("2024-03-02 10:00", "C"),
                OccurrencesEntity("2024-03-02 11:00", "D"),
                OccurrencesEntity("2024-03-02 12:00", "E")
            )).entries.first()
        )

        val count = getOccurrenceCount(multipleEntriesList)
        assertEquals(5, count)
    }


    private fun getOccurrenceCount(occurrenceList: List<MutableMap.MutableEntry<String, List<OccurrencesEntity>>>): Int {
        var count = 0
        occurrenceList.forEach {
            count += it.value.size
        }
        return count
    }

    private fun getMaxAndMinOccurrenceCount(occurrenceList: List<MutableMap.MutableEntry<String, List<OccurrencesEntity>>>): Pair<Map<String, Int>, Map<String, Int>> {
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
}