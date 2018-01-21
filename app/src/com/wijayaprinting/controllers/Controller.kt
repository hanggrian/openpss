package com.wijayaprinting.controllers

import com.wijayaprinting.base.EmployeeContainer
import com.wijayaprinting.Language
import com.wijayaprinting.base.Resourced
import com.wijayaprinting.io.ConfigFile
import com.wijayaprinting.db.Employee
import java.util.*

/** Base class of all controllers. */
abstract class Controller : Resourced, EmployeeContainer {

    override val resources: ResourceBundle = Language.from(ConfigFile.language.get()).resources
    override lateinit var employee: Employee

    private var mExtra: Any? = null

    abstract fun initialize()

    fun setExtra(value: Any?) {
        mExtra = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getExtra(): T = checkNotNull(mExtra as T) { "User models has not been initialized!" }
}