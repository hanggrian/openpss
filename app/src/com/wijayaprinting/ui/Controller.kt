package com.wijayaprinting.ui

import com.wijayaprinting.Language
import com.wijayaprinting.db.dao.Employee
import com.wijayaprinting.io.ConfigFile
import java.util.*

/** Base class of all plate. */
abstract class Controller : Resourced, EmployeeHolder {

    override val resources: ResourceBundle = Language.from(ConfigFile.language.get()).resources
    override lateinit var _employee: Employee

    private var mExtra: Any? = null

    abstract fun initialize()

    fun setExtra(value: Any?) {
        mExtra = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getExtra(): T = checkNotNull(mExtra as T) { "Named model has not been initialized!" }
}