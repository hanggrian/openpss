package com.hendraanggrian.openpss

import com.hendraanggrian.defaults.ReadableDefaults
import com.hendraanggrian.openpss.api.OpenPssApi
import com.hendraanggrian.openpss.data.Employee

interface Component<L, D : ReadableDefaults> {

    val rootLayout: L

    val login: Employee

    val api: OpenPssApi

    val defaults: D
}