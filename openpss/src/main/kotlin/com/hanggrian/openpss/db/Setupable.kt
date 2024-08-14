package com.hanggrian.openpss.db

interface Setupable {
    fun setup(wrapper: ExtendedSession)
}
