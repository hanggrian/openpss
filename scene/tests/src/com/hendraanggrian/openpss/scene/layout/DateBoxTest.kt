package com.hendraanggrian.openpss.scene.layout

import com.hendraanggrian.openpss.scene.NodeTest
import com.hendraanggrian.openpss.time.toJava
import org.joda.time.LocalDate
import org.junit.Test
import kotlin.test.assertEquals

class DateBoxTest : NodeTest<DateBox>() {

    override fun newInstance() = DateBox()

    @Test fun default() = LocalDate.now().let {
        assertEquals(node.dateProperty.value.dayOfWeek, it.dayOfWeek)
        assertEquals(node.dateProperty.value.monthOfYear, it.monthOfYear)
        assertEquals(node.dateProperty.value.year, it.year)
    }

    @Test fun custom() = LocalDate(1992, 5, 20).let {
        node.picker.value = it.toJava()
        assertEquals(node.dateProperty.value.dayOfWeek, it.dayOfWeek)
    }
}