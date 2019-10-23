package com.hendraanggrian.openpss

import com.hendraanggrian.defaults.ReadableDefaults
import com.hendraanggrian.openpss.schema.Employee

interface Component<L, D : ReadableDefaults> {

    val rootLayout: L

    val login: Employee

    val defaults: D
}
