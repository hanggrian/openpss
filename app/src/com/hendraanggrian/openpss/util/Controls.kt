@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.resources.Resourced
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

/** Can't use `ktfx-coroutines` because by the time `consume` is called in coroutine context, it is already too late. */
fun Node.onActionFilter(
    context: CoroutineContext = FX,
    action: suspend CoroutineScope.() -> Unit
) = addEventFilter(ACTION) {
    it.consume()
    launch(context) { action() }
}

inline fun Resourced.yesNoAlert(
    contentTextId: String = R.string.are_you_sure,
    noinline action: () -> Unit
) = confirmAlert(getString(contentTextId), YES, NO) { style() }
    .showAndWait()
    .filter { it == YES }
    .ifPresent { action() }

inline fun <T> ListView<T>.forceRefresh() {
    val temp = items
    items = null
    items = temp
}

@Suppress("UNCHECKED_CAST")
inline fun <T> ChoiceBox<*>.get(): T = value as T