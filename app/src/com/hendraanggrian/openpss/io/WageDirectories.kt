package com.hendraanggrian.openpss.io

import org.joda.time.DateTime
import java.io.File

object WageDirectory : Directory(MainDirectory, "wage")

class WageFile : File(WageDirectory, "${DateTime.now().toString("yyyy-MM-dd HH.mm")}.png")