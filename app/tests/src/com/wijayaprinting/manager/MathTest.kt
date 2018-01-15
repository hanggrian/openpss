package com.wijayaprinting.manager

import com.wijayaprinting.util.rounded
import org.junit.Test
import kotlin.math.round

class MathTest {

    @Test
    fun round() {
        val a = 2.345134
        println(a.rounded)
        println(round(a))
    }
}