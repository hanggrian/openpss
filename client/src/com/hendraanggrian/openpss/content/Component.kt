package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.data.Employee

interface Component<T> {

    val rootLayout: T

    val login: Employee
}