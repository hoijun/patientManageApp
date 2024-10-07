package com.example.patientManageApp

import com.example.patientManageApp.domain.entity.CameraEntity
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class CameraSettingTest {
    @Test
    fun `카메라 인덱스가 음수이고 모든 값이 변경된 경우 true 반환`() {
        val cameraEntity = CameraEntity("", "", "")
        val result = isSaveButtonEnabled(-1, "새 이름", "새 주소", "새 이미지", cameraEntity)
        assertTrue(result)
    }

    @Test
    fun `카메라 인덱스가 음수이고 일부 값만 변경된 경우 false 반환`() {
        val cameraEntity = CameraEntity("", "", "")
        val result = isSaveButtonEnabled(-1, "새 이름", "이전 주소", "이전 이미지", cameraEntity)
        assertFalse(result)
    }

    @Test
    fun `카메라 인덱스가 양수이고 모든 값이 변경된 경우 true 반환`() {
        val cameraEntity = CameraEntity("이전 이름", "이전 주소", "이전 이미지")
        val result = isSaveButtonEnabled(0, "새 이름", "새 주소", "새 이미지", cameraEntity)
        assertTrue(result)
    }

    @Test
    fun `카메라 인덱스가 양수이고 값이 변경되지 않은 경우 false 반환`() {
        val cameraEntity = CameraEntity("이전 이름", "이전 주소", "이전 이미지")
        val result = isSaveButtonEnabled(0, "이전 이름", "이전 주소", "이전 이미지", cameraEntity)
        assertFalse(result)
    }

    @Test
    fun `카메라 인덱스가 양수이고 일부 값이 비어있는 경우 false 반환`() {
        val cameraEntity = CameraEntity("이전 이름", "이전 주소", "이전 이미지")
        val result = isSaveButtonEnabled(0, "", "새 주소", "새 이미지", cameraEntity)
        assertFalse(result)
    }

    @Test
    fun `카메라 인덱스가 양수이고 일부 값만 변경된 경우 true 반환`() {
        val cameraEntity = CameraEntity("이전 이름", "이전 주소", "이전 이미지")
        val result = isSaveButtonEnabled(0, "새 이름", "이전 주소", "이전 이미지", cameraEntity)
        assertTrue(result)
    }

    private fun isSaveButtonEnabled(
        cameraIndex: Int,
        cameraName: String,
        rtspAddress: String,
        backGroundImg: String,
        cameraEntity: CameraEntity
    ): Boolean {
        return if (cameraIndex == -1) {
            cameraEntity.name != cameraName && cameraEntity.rtspAddress != rtspAddress && backGroundImg != cameraEntity.backGroundImg
        } else {
            (cameraEntity.name != cameraName || cameraEntity.rtspAddress != rtspAddress || backGroundImg != cameraEntity.backGroundImg) && (cameraName != "" && rtspAddress != "")
        }
    }

    @Test
    fun `새 카메라 추가 시 중복 없음`() {
        val cameraList = listOf(
            CameraEntity("Camera1", "rtsp://address1", "bg1"),
            CameraEntity("Camera2", "rtsp://address2", "bg2")
        )
        val newCamera = CameraEntity("Camera3", "rtsp://address3", "bg3")
        val result = checkDuplicateCamera(cameraList, newCamera, -1)
        assertFalse(result)
    }

    @Test
    fun `새 카메라 추가 시 이름 중복`() {
        val cameraList = listOf(
            CameraEntity("Camera1", "rtsp://address1", "bg1"),
            CameraEntity("Camera2", "rtsp://address2", "bg2")
        )
        val newCamera = CameraEntity("Camera1", "rtsp://address3", "bg3")
        val result = checkDuplicateCamera(cameraList, newCamera, -1)
        assertTrue(result)
    }

    @Test
    fun `새 카메라 추가 시 주소 중복`() {
        val cameraList = listOf(
            CameraEntity("Camera1", "rtsp://address1", "bg1"),
            CameraEntity("Camera2", "rtsp://address2", "bg2")
        )
        val newCamera = CameraEntity("Camera3", "rtsp://address1", "bg3")
        val result = checkDuplicateCamera(cameraList, newCamera, -1)
        assertTrue(result)
    }

    @Test
    fun `기존 카메라 수정 시 중복 없음`() {
        val cameraList = listOf(
            CameraEntity("Camera1", "rtsp://address1", "bg1"),
            CameraEntity("Camera2", "rtsp://address2", "bg2"),
            CameraEntity("Camera3", "rtsp://address3", "bg3")
        )
        val updatedCamera = CameraEntity("Camera2Updated", "rtsp://address2Updated", "bg2")
        val result = checkDuplicateCamera(cameraList, updatedCamera, 1)
        assertFalse(result)
    }

    @Test
    fun `기존 카메라 수정 시 다른 카메라와 이름 중복`() {
        val cameraList = listOf(
            CameraEntity("Camera1", "rtsp://address1", "bg1"),
            CameraEntity("Camera2", "rtsp://address2", "bg2"),
            CameraEntity("Camera3", "rtsp://address3", "bg3")
        )
        val updatedCamera = CameraEntity("Camera1", "rtsp://address2Updated", "bg2")
        val result = checkDuplicateCamera(cameraList, updatedCamera, 1)
        assertTrue(result)
    }

    @Test
    fun `기존 카메라 수정 시 다른 카메라와 주소 중복`() {
        val cameraList = listOf(
            CameraEntity("Camera1", "rtsp://address1", "bg1"),
            CameraEntity("Camera2", "rtsp://address2", "bg2"),
            CameraEntity("Camera3", "rtsp://address3", "bg3")
        )
        val updatedCamera = CameraEntity("Camera2Updated", "rtsp://address3", "bg2")
        val result = checkDuplicateCamera(cameraList, updatedCamera, 1)
        assertTrue(result)
    }

    @Test
    fun `기존 카메라 수정 시 자기 자신과 동일`() {
        val cameraList = listOf(
            CameraEntity("Camera1", "rtsp://address1", "bg1"),
            CameraEntity("Camera2", "rtsp://address2", "bg2"),
            CameraEntity("Camera3", "rtsp://address3", "bg3")
        )
        val updatedCamera = CameraEntity("Camera2", "rtsp://address2", "bg2")
        val result = checkDuplicateCamera(cameraList, updatedCamera, 1)
        assertFalse(result)
    }

    private fun checkDuplicateCamera(
        cameraList: List<CameraEntity>,
        cameraEntity: CameraEntity,
        cameraIndex: Int
    ): Boolean {
        if (cameraIndex == -1)
            return cameraList.any { it.name == cameraEntity.name || it.rtspAddress == cameraEntity.rtspAddress }
        return cameraList.any {
            it != cameraList[cameraIndex] && (it.name == cameraEntity.name || it.rtspAddress == cameraEntity.rtspAddress) }
    }
}