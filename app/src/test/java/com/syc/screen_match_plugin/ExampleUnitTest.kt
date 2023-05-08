package com.syc.screen_match_plugin

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
        var s = "asdf"
        s = s.replaceRange(0,1,"afdadf")
        println("----$s")
    }
}