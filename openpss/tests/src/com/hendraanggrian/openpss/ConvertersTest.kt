package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.content.numberConverter
import ktfx.util.invoke
import org.junit.Test
import kotlin.test.assertEquals

class ConvertersTest {

    @Test fun numberConverter() {
        assertEquals("12", numberConverter(12))
        assertEquals("1,234", numberConverter(1234))
        assertEquals("1,234,567", numberConverter(1234567))

        assertEquals(12, numberConverter("12"))
        assertEquals(1234, numberConverter("1,234"))
        assertEquals(1234567, numberConverter("1,234,567"))
    }
}