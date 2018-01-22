package com.wijayaprinting.ui.attendance

import javafx.collections.ObservableSet
import kotfx.mutableObservableSetOf

/** Defines an execution that can be undone. */
data class Undoable @JvmOverloads constructor(
        var name: String? = null,
        private val actions: ObservableSet<() -> Unit> = mutableObservableSetOf()
) {
    val isValid: Boolean get() = name != null && actions.isNotEmpty()

    fun addAction(action: () -> Unit) {
        actions.add(action)
    }

    fun undo() {
        actions.forEach { it() }
        actions.clear()
    }
}