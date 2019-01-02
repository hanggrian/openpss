package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.api.OpenPssApi
import com.hendraanggrian.openpss.data.Employee

interface Component<L, S : Setting<E>, E> {

    val rootLayout: L

    val login: Employee

    val api: OpenPssApi

    val setting: S
}