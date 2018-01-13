package com.wijayaprinting.readers

import com.wijayaprinting.core.Listable
import com.wijayaprinting.core.Resourced
import com.wijayaprinting.data.Attendee
import javafx.collections.ObservableList
import javafx.stage.FileChooser
import kotfx.observableListOf
import java.io.File

/** A file readers that generates collection of [com.wijayaprinting.data.Attendee] given input file. */
abstract class Reader {

    /** Identifier of a reader. */
    abstract val name: String

    /** Expected file extensions for [FileChooser.ExtensionFilter]. */
    abstract val extensions: Array<String>

    /**
     * The reading process is executed in background thread.
     * During its long operation, exception throwing may happen in [read].
     */
    @Throws(Exception::class)
    abstract fun read(resourced: Resourced, file: File): Collection<Attendee>

    override fun toString(): String = name

    companion object : Listable<Reader> {
        override fun listAll(): ObservableList<Reader> = observableListOf(EClockingReader)
    }
}