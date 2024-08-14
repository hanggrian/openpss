package com.hanggrian.openpss.control

import com.hanggrian.openpss.util.toJava
import org.joda.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class DateBoxTest : NodeTest<DateBox>() {
    override fun newInstance() = DateBox()

    @Test
    fun default() =
        LocalDate.now().let {
            assertEquals(node.valueProperty.value.dayOfWeek, it.dayOfWeek)
            assertEquals(node.valueProperty.value.monthOfYear, it.monthOfYear)
            assertEquals(node.valueProperty.value.year, it.year)
        }

    @Test
    fun custom() =
        LocalDate(1992, 5, 20).let {
            node.picker.value = it.toJava()
            assertEquals(node.valueProperty.value.dayOfWeek, it.dayOfWeek)
        }
}
