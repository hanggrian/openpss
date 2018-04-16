package com.hendraanggrian.openpss.ui.wage.record

/** Defines an execution that can be undone. */
data class Undoable(
    var name: String? = null,
    private val actions: MutableSet<() -> Unit> = mutableSetOf()
) {
    val isValid: Boolean get() = name != null && actions.isNotEmpty()

    fun addAction(action: () -> Unit) {
        actions += action
    }

    fun undo() {
        actions.forEach { it() }
        actions.clear()
    }
}