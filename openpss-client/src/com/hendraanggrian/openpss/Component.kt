package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.schema.Employee
import com.hendraanggrian.prefs.ReadablePrefs

interface Component<L, P : ReadablePrefs> {

    val rootLayout: L

    val login: Employee

    val prefs: P
}
