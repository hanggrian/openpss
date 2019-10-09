package com.hendraanggrian.openpss.ui.wage

import com.google.common.collect.LinkedHashMultimap
import javafx.collections.ObservableList
import javafx.stage.FileChooser
import ktfx.collections.toObservableList
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import java.io.File

/**
 * Compatible with e Clocking fingerprint reader.
 * Tested name: `2.1.015`.
 */
object EClockingReader : WageReader("e Clocking", "*.xlsx", {
    val multimap = LinkedHashMultimap.create<Attendee, DateTime>()
    inputStream().use { stream ->
        XSSFWorkbook(stream).use { workbook ->
            workbook.getSheetAt(1).iterator().asSequence().drop(5).forEach { row ->
                val dept = row.getCell(1).stringCellValue
                val name = row.getCell(2).stringCellValue
                val no = row.getCell(3).numericCellValue.toInt()
                val date = LocalDate.fromDateFields(row.getCell(4).dateCellValue)
                multimap.putAll(Attendee(no, name, dept), (6 until 17)
                    .map { row.getCell(it) }
                    .filter { it.cellType == CellType.NUMERIC }
                    .map { date.toDateTime(LocalTime.fromDateFields(it.dateCellValue)) })
            }
        }
    }
    multimap.keySet().map { attendee ->
        attendee.attendances.addAllRevertible(multimap.get(attendee))
        attendee
    }
})

object TestReader : WageReader("Test", "*.*", {
    listOf(
        Attendee(1, "Without role"),
        Attendee(2, "With role", "Admin")
    )
})

/** A file readers that generates actions of [Attendee] given input file. */
sealed class WageReader(

    /** Identifier of a reader. */
    val name: String,

    /** Expected file extension for [FileChooser.ExtensionFilter]. */
    val extension: String,

    /**
     * The reading process is executed in background thread.
     * During its long operation, exception throwing may happen in [read].
     */
    private val internalRead: suspend File.() -> Collection<Attendee>
) {

    @Throws(Exception::class)
    suspend fun read(file: File): Collection<Attendee> = internalRead(file)

    override fun toString(): String = name

    companion object {

        private inline val readers: List<WageReader>
            get() = listOf(
                EClockingReader,
                TestReader
            )

        fun listAll(): ObservableList<WageReader> = readers.toObservableList()

        fun of(name: String): WageReader =
            readers.singleOrNull { it.name == name } ?: EClockingReader
    }
}
