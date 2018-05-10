package com.hendraanggrian.openpss.ui.wage.readers

import com.google.common.collect.LinkedHashMultimap
import com.hendraanggrian.openpss.ui.wage.Attendee
import kotlinx.coroutines.experimental.async
import org.apache.poi.ss.usermodel.CellType.NUMERIC
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import java.io.File

/**
 * Compatible with e Clocking fingerprint reader.
 * Tested version: `2.1.015`.
 */
object EClockingReader : Reader() {

    private const val SHEET_RAW_ATTENDANCE_LOGS = 1
    private const val CELL_DEPT = 1
    private const val CELL_NAME = 2
    private const val CELL_NO = 3
    private const val CELL_DATE = 4
    private const val CELL_RECORD_START = 6
    private const val CELL_RECORD_END = 17

    override val name: String = "e Clocking"

    override val extensions: Array<String> = arrayOf("*.xlsx")

    override suspend fun read(file: File): Collection<Attendee> = async {
        val multimap = LinkedHashMultimap.create<Attendee, DateTime>()
        file.inputStream().use {
            XSSFWorkbook(it).use {
                it.getSheetAt(SHEET_RAW_ATTENDANCE_LOGS)
                    .iterator()
                    .asSequence()
                    .drop(5)
                    .forEach { row ->
                        val dept = row.getCell(CELL_DEPT).stringCellValue
                        val name = row.getCell(CELL_NAME).stringCellValue
                        val no = row.getCell(CELL_NO).numericCellValue.toInt()
                        val date = LocalDate.fromDateFields(row.getCell(CELL_DATE).dateCellValue)
                        multimap.putAll(Attendee(no, name, dept), (CELL_RECORD_START until CELL_RECORD_END)
                            .map { row.getCell(it) }
                            .filter { it.cellTypeEnum == NUMERIC }
                            .map { date.toDateTime(LocalTime.fromDateFields(it.dateCellValue)) })
                    }
            }
        }
        multimap.keySet().map { attendee ->
            attendee.attendances.addAllRevertible(multimap.get(attendee))
            attendee
        }
    }.await()
}