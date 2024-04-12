package io.github.iosephknecht.annotations.substitutor

import androidx.test.platform.app.InstrumentationRegistry
import io.github.iosephknecht.overridden.annotations.RegularTest

import org.junit.Test

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RegularTest
class ExampleRegularTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("io.github.iosephknecht.annotations.substitutor", appContext.packageName)
    }
}