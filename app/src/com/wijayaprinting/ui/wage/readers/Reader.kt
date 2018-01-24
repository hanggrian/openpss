package com.wijayaprinting.ui.wage.readers

import com.wijayaprinting.ui.Listable
import com.wijayaprinting.ui.wage.Attendee
import javafx.collections.ObservableList
import javafx.stage.FileChooser
import kotfx.observableListOf
import java.io.File

/** A file readers that generates collection of [com.wijayaprinting.model.Attendee] given input file. */
abstract class Reader {

    abstract val name: String

    /** Expected file extensions for [FileChooser.ExtensionFilter]. */
    abstract val extensions: Array<String>

    /**
     * The reading process is executed in background thread.
     * During its long operation, exception throwing may happen in [read].
     */
    @Throws(Exception::class)
    abstract fun read(file: File): Collection<Attendee>

    /** Identifier of a reader. */
    override fun toString(): String = name

    companion object : Listable<Reader> {
        override fun listAll(): ObservableList<Reader> = observableListOf(EClockingReader)
    }
}