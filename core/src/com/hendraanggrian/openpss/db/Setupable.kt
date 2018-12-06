package com.hendraanggrian.openpss.db

interface Setupable {

    fun setup(wrapper: SessionWrapper)
}