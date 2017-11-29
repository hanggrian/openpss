package com.wijayaprinting.manager.reader

import com.google.common.collect.LinkedHashMultimap
import com.wijayaprinting.manager.data.Employee
import org.apache.commons.lang3.SystemUtils
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime
import java.io.File

/**
 * @see Reader
 */
class EClockingReader : Reader {

    override fun toString(): String = "e Clocking 2.1.015"

    @Throws(Exception::class)
    override fun read(file: File): Collection<Employee> {
        val multimap = LinkedHashMultimap.create<Employee, DateTime>()
        val stream = file.inputStream()
        val workbook = XSSFWorkbook(stream)
        val sheet = workbook.getSheetAt(SHEET_RAW_ATTENDANCE_LOGS)
        sheet.iterator().asSequence().forEachIndexed { rowIndex, row ->
            // skips right to employeeBox
            if (rowIndex > 4) {
                val dept = row.getCell(CELL_DEPT).stringCellValue
                val name = row.getCell(CELL_NAME).stringCellValue
                val no = row.getCell(CELL_NO).numericCellValue.toInt()
                val date = DateTime(row.getCell(CELL_DATE).dateCellValue.time)
                val day = date.dayOfMonth
                val month = date.monthOfYear
                val year = date.year
                val employee = Employee(no, name, dept)
                for (CELL_RECORD in CELL_RECORD_START until CELL_RECORD_END) {
                    row.getCell(CELL_RECORD).let {
                        if (it.cellTypeEnum == CellType.NUMERIC) {
                            val record = DateTime(it.dateCellValue.time)
                            val hour = record.hourOfDay
                            val minute = record.minuteOfHour
                            val attendance = DateTime(year, month, day, hour, minute)
                            multimap.put(employee, when (true) {
                                SystemUtils.IS_OS_WINDOWS -> attendance.plusMinutes(18)
                                SystemUtils.IS_OS_MAC -> attendance.minusMinutes(7)
                                else -> attendance
                            })
                        }
                    }
                }
            }
        }
        workbook.close()
        stream.close()

        val set = mutableSetOf<Employee>()
        for (attendee in multimap.keySet()) {
            attendee.addAllAttendances(multimap.get(attendee))
            set.add(attendee)
        }
        return set
    }

    companion object {
        private const val SHEET_RAW_ATTENDANCE_LOGS = 1
        private const val CELL_DEPT = 1
        private const val CELL_NAME = 2
        private const val CELL_NO = 3
        private const val CELL_DATE = 4
        private const val CELL_RECORD_START = 6
        private const val CELL_RECORD_END = 17
    }
}