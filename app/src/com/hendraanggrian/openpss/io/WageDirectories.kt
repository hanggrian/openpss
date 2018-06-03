package com.hendraanggrian.openpss.io

import org.joda.time.LocalDate
import org.joda.time.LocalTime
import java.io.File

object WageDirectory : Directory(MainDirectory, "wage")

open class WageContentDirectory(date: LocalDate) : Directory(WageDirectory, date.toString("yyyy-MM-dd")) {

    companion object : WageContentDirectory(LocalDate.now())
}

open class WageContentFile(time: LocalTime) : File(WageContentDirectory, "${time.toString("HH:mm")}.png") {

    companion object : WageContentFile(LocalTime.now())
}