package com.hendraanggrian.openpss.ui.wage.readers

import com.hendraanggrian.openpss.ui.wage.Attendee
import java.io.File

object TestReader : Reader() {

    override val name: String get() = "Test"

    override val extensions: Array<String> get() = arrayOf("*.*")

    override suspend fun read(file: File): Collection<Attendee> = listOf(
        Attendee(1, "Without role"),
        Attendee(2, "With role", "Admin")
    )
}