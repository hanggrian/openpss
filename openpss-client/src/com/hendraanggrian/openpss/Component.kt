package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.schema.Employee
import com.hendraanggrian.prefy.ReadablePreferences

interface Component<L, P : ReadablePreferences> {

    val rootLayout: L

    val login: Employee

    val prefs: P
}
