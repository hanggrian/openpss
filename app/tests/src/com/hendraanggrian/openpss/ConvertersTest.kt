package com.hendraanggrian.openpss

import org.junit.Test
import kotlin.test.assertEquals

class ConvertersTest {

    @Test fun numberConverter() {
        assertEquals("12", numberConverter.toString(12))
        assertEquals("1,234", numberConverter.toString(1234))
        assertEquals("1,234,567", numberConverter.toString(1234567))
    }
}