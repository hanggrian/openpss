package com.hendraanggrian.openpss.scene.layout

import com.hendraanggrian.openpss.scene.NodeTest
import org.joda.time.LocalTime
import org.junit.Test
import kotlin.test.assertEquals

class TimeBoxTest : NodeTest<TimeBox>() {

    override fun newInstance() = TimeBox()

    @Test fun default() = LocalTime.MIDNIGHT.let {
        assertEquals(node.timeProperty.value.hourOfDay, it.hourOfDay)
        assertEquals(node.timeProperty.value.minuteOfHour, it.minuteOfHour)
    }

    @Test fun custom() = LocalTime(12, 30).let {
        node.hourField.value = it.hourOfDay
        node.minuteField.value = it.minuteOfHour
        assertEquals(node.timeProperty.value, it)
    }
}