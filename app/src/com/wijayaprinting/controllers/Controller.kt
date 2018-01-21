package com.wijayaprinting.controllers

import com.wijayaprinting.core.EmployeeContainer
import com.wijayaprinting.core.Language
import com.wijayaprinting.core.Resourced
import com.wijayaprinting.io.PreferencesFile
import com.wijayaprinting.nosql.Employee
import java.util.*

/** Base class of all controllers. */
abstract class Controller : Resourced, EmployeeContainer {

    override val resources: ResourceBundle = Language.from(PreferencesFile.language.get()).resources
    override lateinit var employee: Employee

    private var mExtra: Any? = null

    abstract fun initialize()

    fun setExtra(value: Any?) {
        mExtra = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getExtra(): T = checkNotNull(mExtra as T) { "User data has not been initialized!" }
}