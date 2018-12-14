package com.hendraanggrian.openpss.ui.wage.readers

import com.hendraanggrian.openpss.ui.wage.Attendee
import javafx.collections.ObservableList
import javafx.stage.FileChooser
import ktfx.collections.toObservableList
import java.io.File

/** A file readers that generates actions of [Attendee] given input file. */
open class Reader(

    /** Identifier of a reader. */
    val name: String,

    /** Expected file extension for [FileChooser.ExtensionFilter]. */
    val extension: String,

    /**
     * The reading process is executed in background thread.
     * During its long operation, exception throwing may happen in [read].
     */
    private val internalRead: suspend File.() -> Collection<Attendee>
) {

    @Throws(Exception::class)
    suspend fun read(file: File): Collection<Attendee> = internalRead(file)

    override fun toString(): String = name

    companion object {

        private val READERS: List<Reader> get() = listOf(EClockingReader, TestReader)

        fun listAll(): ObservableList<Reader> = READERS.toObservableList()

        fun of(name: String): Reader = READERS.single { it.name == name }
    }
}