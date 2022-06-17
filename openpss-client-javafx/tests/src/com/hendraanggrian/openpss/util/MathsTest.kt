package com.hendraanggrian.openpss.util

import org.junit.Test
import kotlin.test.assertEquals

class MathsTest {

    @Test fun round() {
        val decimal = 2.345134
        assertEquals(decimal.round(), 2.35)
    }
}
