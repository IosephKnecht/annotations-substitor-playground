package io.github.iosephknecht.annotations.substitutor

import io.github.iosephknecht.overridden.annotations.ParameterizedTest
import org.junit.Test
import org.junit.runners.Parameterized.Parameters

@ParameterizedTest
internal class ExampleParameterizedTest {

    @Test
    fun check() {

    }

    companion object {

        @JvmStatic
        @Parameters
        fun data() = listOf("")
    }
}