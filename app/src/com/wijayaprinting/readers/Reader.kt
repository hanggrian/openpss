package com.wijayaprinting.readers

import com.wijayaprinting.base.Listable
import com.wijayaprinting.base.Resourced
import com.wijayaprinting.models.Attendee
import javafx.collections.ObservableList
import javafx.stage.FileChooser
import kotfx.observableListOf
import java.io.File

/** A file readers that generates collection of [com.wijayaprinting.models.Attendee] given input file. */
interface Reader {

    /** Expected file extensions for [FileChooser.ExtensionFilter]. */
    val extensions: Array<out String>

    /** Identifier of a reader. */
    override fun toString(): String

    /**
     * The reading process is executed in background thread.
     * During its long operation, exception throwing may happen in [read].
     */
    @Throws(Exception::class)
    fun read(resourced: Resourced, file: File): Collection<Attendee>

    companion object : Listable<Reader> {
        override fun listAll(): ObservableList<Reader> = observableListOf(EClockingReader)
    }
}