package com.hendraanggrian.openpss.io

import org.joda.time.DateTime
import java.io.File

class WageFile : File(WageDirectory, "${DateTime.now().toString("yyyy-MM-dd HH.mm")}.png")