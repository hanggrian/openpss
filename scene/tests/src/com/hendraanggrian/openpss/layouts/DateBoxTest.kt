package com.hendraanggrian.openpss.layouts

import com.hendraanggrian.openpss.scene.NodeTest
import com.hendraanggrian.openpss.core.toJava
import com.hendraanggrian.openpss.scene.layouts.DateBox
import org.joda.time.LocalDate
import org.junit.Test
import kotlin.test.assertEquals

class DateBoxTest : NodeTest<DateBox>() {

    override fun newInstance() = DateBox()

    @Test fun default() = LocalDate.now().let {
        assertEquals(node.valueProperty.value.dayOfWeek, it.dayOfWeek)
        assertEquals(node.valueProperty.value.monthOfYear, it.monthOfYear)
        assertEquals(node.valueProperty.value.year, it.year)
    }

    @Test fun custom() = LocalDate(1992, 5, 20).let {
        node.picker.value = it.toJava()
        assertEquals(node.valueProperty.value.dayOfWeek, it.dayOfWeek)
    }
}