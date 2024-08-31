package com.example.patientManageApp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var appContext: Context

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testUriToBitmap() = runBlocking {
        val uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/aidetection-d68f6.appspot.com/o/Users%2FwR5dbiFCl3OGCffPbsdWXh6Nf9G3%2F2024-08-31_185047.jpg?alt=media&token=3a8dc38b-036e-48de-97ff-0e5d3ed43412")
        val bitmap = uriToBitmap(uri)

        assertNotNull("Bitmap should not be null", bitmap)
        assertTrue("Bitmap width should be greater than 0", bitmap!!.width > 0)
        assertTrue("Bitmap height should be greater than 0", bitmap.height > 0)
    }

    private suspend fun uriToBitmap(uri: Uri): Bitmap? {
        val loader = ImageLoader(appContext)
        val request = ImageRequest.Builder(appContext)
            .data(uri)
            .allowHardware(false)
            .build()

        return when (val result = loader.execute(request)) {
            is SuccessResult -> (result.drawable as? BitmapDrawable)?.bitmap
            is ErrorResult -> {
                null
            }
        }
    }
}