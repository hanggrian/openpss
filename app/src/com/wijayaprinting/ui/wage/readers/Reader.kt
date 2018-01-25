package com.wijayaprinting.ui.wage.readers

import com.wijayaprinting.ui.wage.Attendee
import javafx.collections.ObservableList
import javafx.stage.FileChooser
import kotfx.observableListOf
import java.io.File
import java.io.IOException

/** A file readers that generates collection of [Attendee] given input file. */
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
    abstract fun read(file: File): Collection<Attendee>

    override fun toString(): String = name

    companion object {
        fun listAll(): ObservableList<Reader> = observableListOf(EClockingReader)
    }
}