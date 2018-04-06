@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.utils

import javafx.event.ActionEvent.ACTION
import javafx.scene.Node
import javafx.scene.control.ButtonType.NO
import javafx.scene.control.ButtonType.YES
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ListView
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.launch
import ktfx.coroutines.FX
import ktfx.scene.control.confirmAlert
import kotlin.coroutines.experimental.CoroutineContext

fun Node.onActionFilter(
    context: CoroutineContext = FX,
    action: suspend CoroutineScope.() -> Unit
) = addEventFilter(ACTION) {
    it.consume()
    launch(context) { action() }
}

inline fun yesNoAlert(contentText: String, noinline action: () -> Unit) = confirmAlert(contentText, YES, NO)
    .showAndWait()
    .filter { it == YES }
    .ifPresent { action() }

inline fun <T> ListView<T>.forceRefresh() = items.let {
    items = null
    items = it
}

@Suppress("UNCHECKED_CAST")
inline fun <T> ChoiceBox<*>.get(): T = value as T