package com.hendraanggrian.openpss.layouts

import com.hendraanggrian.openpss.scene.NodeTest
import org.joda.time.LocalTime
import org.junit.Test
import kotlin.test.assertEquals

class TimeBoxTest : NodeTest<TimeBox>() {

    override fun newInstance() = TimeBox()

    @Test fun default() = LocalTime.MIDNIGHT.let {
        assertEquals(node.valueProperty().value.hourOfDay, it.hourOfDay)
        assertEquals(node.valueProperty().value.minuteOfHour, it.minuteOfHour)
    }

    @Test fun custom() = LocalTime(12, 30).let {
        node.hourField.value = it.hourOfDay
        node.minuteField.value = it.minuteOfHour
        assertEquals(node.valueProperty().value, it)
    }
}