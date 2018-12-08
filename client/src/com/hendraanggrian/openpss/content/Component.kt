package com.hendraanggrian.openpss.content

import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.db.schemas.Employee

interface Component<T> {

    val rootLayout: T

    val login: Employee

    suspend fun isAdmin(api: OpenPSSApi): Boolean = api.isAdmin(login.name)
}