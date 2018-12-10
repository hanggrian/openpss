package com.hendraanggrian.openpss.io

import java.io.File

open class Directory : File {

    constructor(parent: String, child: String) : super(parent, child)

    constructor(parent: Directory, child: String) : super(parent, child)

    init {
        @Suppress("LeakingThis") mkdirs()
    }
}