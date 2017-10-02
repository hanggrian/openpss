package com.wijayaprinting.javafx.reader

import com.wijayaprinting.javafx.data.Employee
import javafx.collections.ObservableList
import kotfx.collections.observableListOf
import java.io.File

/**
 * An xlsx reader that generates collection of employees given input file.
 * The actual reading process is helped by rxjava, therefore long operation and exception throwing may happen in [Reader.read].
 *
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
interface Reader {

    @Throws(Exception::class)
    fun read(file: File): Collection<Employee>

    companion object {
        private var ALL: ObservableList<Reader>? = null

        fun listAll(): ObservableList<Reader> {
            if (ALL == null) {
                ALL = observableListOf(EClockingReader())
            }
            return ALL!!
        }

        fun parse(name: String): Reader {
            checkNotNull(ALL, { "Call listAll() before parse()!" })
            val reader = ALL!!.firstOrNull { it.toString() == name }
            checkNotNull(reader, { "No reader found with that name!" })
            return reader!!
        }
    }
}