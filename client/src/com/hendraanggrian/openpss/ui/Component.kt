package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.api.OpenPssApi
import com.hendraanggrian.openpss.data.Employee

interface Component<T> {

    val rootLayout: T

    val login: Employee

    val api: OpenPssApi
}