package com.hendraanggrian.openpss.io

import java.io.File

object TempDirectory : Directory(MainDirectory, ".temp")

class TempFile(name: String) : File(TempDirectory, name)