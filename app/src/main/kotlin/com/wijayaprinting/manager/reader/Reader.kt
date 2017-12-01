package com.wijayaprinting.manager.reader

import com.wijayaprinting.manager.data.Employee
import javafx.collections.ObservableList
import kotfx.collections.observableListOf
import java.io.File

/** An xlsx reader that generates collection of employees given input file. */
interface Reader {

    /** The actual reading process is helped by rxjava, therefore long operation and exception throwing may happen in [Reader.read]. */
    @Throws(Exception::class)
    fun read(file: File): Collection<Employee>

    /** [toString] must be overriden since it is used as identifier to reader. */
    override fun toString(): String

    companion object {
        fun listAll(): ObservableList<Reader> = observableListOf(EClockingReader)
    }
}