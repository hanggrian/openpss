package com.hanggrian.openpss.ui.wage.readers

import com.hanggrian.openpss.ui.wage.Attendee
import javafx.collections.ObservableList
import javafx.stage.FileChooser
import ktfx.collections.toObservableList
import java.io.File
import java.io.IOException

/** A file readers that generates actions of [Attendee] given input file. */
abstract class Reader {
    /** Identifier of a reader. */
    abstract val name: String

    /** Expected file extensions for [FileChooser.ExtensionFilter]. */
    abstract val extensions: Array<String>

    /**
     * The reading process is executed in background thread.
     * During its long operation, exception throwing may happen in [read].
     */
    @Throws(IOException::class)
    abstract suspend fun read(file: File): Collection<Attendee>

    override fun toString(): String = name

    companion object {
        private val READERS get() = listOf(EClockingReader, TestReader)

        fun listAll(): ObservableList<Reader> = READERS.toObservableList()

        fun of(name: String): Reader = READERS.single { it.name == name }
    }
}
