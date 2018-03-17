package com.hendraanggrian.openpss.ui.wage.readers

import com.google.common.collect.LinkedHashMultimap.create
import com.hendraanggrian.openpss.ui.wage.Attendee
import kotlinx.coroutines.experimental.async
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC
import org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS
import org.apache.poi.ss.usermodel.CellType.NUMERIC
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime
import java.io.File

/** Compatible with e Clocking fingerprint reader version 2.1.015. */
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
        val multimap = create<Attendee, DateTime>()
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
                        val date = DateTime(row.getCell(CELL_DATE).dateCellValue)
                        val day = date.dayOfMonth
                        val month = date.monthOfYear
                        val year = date.year
                        multimap.putAll(Attendee(no, name, dept), (CELL_RECORD_START until CELL_RECORD_END)
                            .map { row.getCell(it) }
                            .filter { it.cellTypeEnum == NUMERIC }
                            .map {
                                val record = DateTime(it.dateCellValue)
                                val attendance = DateTime(year, month, day, record.hourOfDay, record.minuteOfHour)
                                when (true) {
                                    IS_OS_WINDOWS -> attendance.plusMinutes(18)
                                    IS_OS_MAC -> attendance.minusMinutes(7)
                                    else -> attendance
                                }
                            })
                    }
            }
        }
        multimap.keySet().map { attendee ->
            attendee.attendances.addAllRevertable(multimap.get(attendee))
            attendee
        }
    }.await()
}