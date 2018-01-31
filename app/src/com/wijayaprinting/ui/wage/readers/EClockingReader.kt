package com.wijayaprinting.ui.wage.readers

import com.google.common.collect.LinkedHashMultimap
import com.wijayaprinting.ui.wage.Attendee
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC
import org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS
import org.apache.poi.ss.usermodel.CellType.NUMERIC
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime
import java.io.File

/** A custom readers to third-party software e Clocking fingerprint reader. */
object EClockingReader : Reader() {

    private const val SHEET_RAW_ATTENDANCE_LOGS = 1
    private const val CELL_DEPT = 1
    private const val CELL_NAME = 2
    private const val CELL_NO = 3
    private const val CELL_DATE = 4
    private const val CELL_RECORD_START = 6
    private const val CELL_RECORD_END = 17

    override val name: String get() = "e Clocking 2.1.015"

    override val extensions: Array<String> get() = arrayOf("*.xlsx")

    override fun read(file: File): Collection<Attendee> {
        val multimap = LinkedHashMultimap.create<Attendee, DateTime>()
        file.inputStream().use { stream ->
            val workbook = XSSFWorkbook(stream)
            val sheet = workbook.getSheetAt(SHEET_RAW_ATTENDANCE_LOGS)
            sheet.iterator().asSequence().drop(5).forEach { row ->
                val dept = row.getCell(CELL_DEPT).stringCellValue
                val name = row.getCell(CELL_NAME).stringCellValue
                val no = row.getCell(CELL_NO).numericCellValue.toInt()
                val date = DateTime(row.getCell(CELL_DATE).dateCellValue.time)
                val day = date.dayOfMonth
                val month = date.monthOfYear
                val year = date.year
                multimap.putAll(Attendee(no, name, dept), (CELL_RECORD_START until CELL_RECORD_END)
                        .map { row.getCell(it) }
                        .filter { it.cellTypeEnum == NUMERIC }
                        .map {
                            val record = DateTime(it.dateCellValue.time)
                            val hour = record.hourOfDay
                            val minute = record.minuteOfHour
                            val attendance = DateTime(year, month, day, hour, minute)
                            when (true) {
                                IS_OS_WINDOWS -> attendance.plusMinutes(18)
                                IS_OS_MAC -> attendance.minusMinutes(7)
                                else -> attendance
                            }
                        })
            }
            workbook.close()
        }
        val set = mutableSetOf<Attendee>()
        for (attendee in multimap.keySet()) {
            attendee.attendances.addAllRevertable(multimap.get(attendee))
            set.add(attendee)
        }
        return set
    }
}