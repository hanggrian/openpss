@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.getStyle
import javafx.event.ActionEvent.ACTION
import javafx.scene.Node
import javafx.scene.control.ButtonType.NO
import javafx.scene.control.ButtonType.YES
import javafx.scene.control.ListView
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import ktfx.scene.control.styledConfirmAlert
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Can't use `javafxx-coroutines` because by the time `consume`
 * is called in coroutine context, it is already too late.
 */
fun Node.onActionFilter(
    context: CoroutineContext = Dispatchers.JavaFx,
    action: suspend CoroutineScope.() -> Unit
) = addEventFilter(ACTION) {
    it.consume()
    GlobalScope.launch(context) { action() }
}

fun Resourced.yesNoAlert(
    contentTextId: String = R.string.are_you_sure,
    action: () -> Unit
) = styledConfirmAlert(getStyle(R.style.openpss), getString(contentTextId), YES, NO)
    .showAndWait()
    .filter { it == YES }
    .ifPresent { action() }

fun <T> ListView<T>.forceRefresh() {
    val temp = items
    items = null
    items = temp
}