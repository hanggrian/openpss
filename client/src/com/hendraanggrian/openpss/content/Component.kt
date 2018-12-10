package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.db.schemas.Employee

interface Component<T> {

    val rootLayout: T

    val login: Employee
}