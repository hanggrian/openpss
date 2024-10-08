package com.hanggrian.openpss.control

import kotlin.test.Test
import kotlin.test.assertEquals

class IntFieldTest : NodeTest<IntField>() {
    override fun newInstance() = IntField()

    @Test
    fun setText() {
        node.text = "12"
        assertEquals(node.value, 12)
    }

    @Test
    fun setValue() {
        node.value = 21
        assertEquals(node.text, "21")
    }
}
