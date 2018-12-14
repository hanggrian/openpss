package com.hendraanggrian.openpss.ui.wage.readers

import com.hendraanggrian.openpss.ui.wage.Attendee

object TestReader : Reader("Test", "*.*", {
    listOf(
        Attendee(1, "Without role"),
        Attendee(2, "With role", "Admin")
    )
})