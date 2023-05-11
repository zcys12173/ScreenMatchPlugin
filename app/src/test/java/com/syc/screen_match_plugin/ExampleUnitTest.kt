package com.syc.screen_match_plugin

import org.junit.Test

import org.junit.Assert.*
import java.io.File
import java.nio.file.FileSystems

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val s = "**/app/"
        val matcher = FileSystems.getDefault().getPathMatcher("glob:$s")
        val file = File("/Users/syc/AndroidStudioProjects/ScreenMatchPlugin/app/src/main/res.xml")
        val result = matcher.matches(file.toPath())
        println("-----------------------$result")
    }
}