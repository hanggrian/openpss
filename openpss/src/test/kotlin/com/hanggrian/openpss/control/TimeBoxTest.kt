package com.hanggrian.openpss.control

import com.hanggrian.openpss.util.toJava
import org.joda.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeBoxTest : NodeTest<TimeBox>() {
    override fun newInstance() = TimeBox()

    @Test
    fun default() =
        LocalTime.MIDNIGHT.let {
            assertEquals(node.valueProperty.value.hourOfDay, it.hourOfDay)
            assertEquals(node.valueProperty.value.minuteOfHour, it.minuteOfHour)
        }

    @Test
    fun custom() =
        LocalTime(12, 30).let {
            node.picker.value = it.toJava()
            assertEquals(node.valueProperty.value, it)
        }
}
