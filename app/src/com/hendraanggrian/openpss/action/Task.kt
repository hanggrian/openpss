package com.hendraanggrian.openpss.action

import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Log
import com.hendraanggrian.openpss.db.schemas.Logs

abstract class Task<T> {

    abstract fun SessionWrapper.action(): T

    abstract val message: String

    open fun onComplete(result: T) {
    }

    fun execute(employee: Employee, session: SessionWrapper) {
        val result = session.action()
        session.run {
            Logs += Log(employee.id, message)
        }
        onComplete(result)
    }
}