package com.wijayaprinting.manager.reader

import com.wijayaprinting.manager.data.Attendee
import com.wijayaprinting.manager.reader.Reader.Companion.toString
import javafx.collections.ObservableList
import javafx.stage.FileChooser
import kotfx.observableListOf
import java.io.File

/** A file reader that generates collection of employees given input file. */
interface Reader {

    /** [toString] must be overriden since it is used as identifier to reader. */
    override fun toString(): String

    /** Expected file extensions for [FileChooser.ExtensionFilter]. */
    val extensions: Array<String>

    /**
     * The actual reading process is helped by RxJava in computation thread.
     * During its long operation, exception throwing may happen in [Reader.read].
     */
    @Throws(Exception::class)
    fun read(file: File): Collection<Attendee>

    companion object {
        fun listAll(): ObservableList<Reader> = observableListOf(EClockingReader)
    }
}