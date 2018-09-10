@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.getStyle
import javafx.event.ActionEvent.ACTION
import javafx.scene.Node
import javafx.scene.control.ButtonType.NO
import javafx.scene.control.ButtonType.YES
import javafx.scene.control.Control
import javafx.scene.control.ListView
import javafxx.coroutines.FX
import javafxx.scene.control.styledConfirmAlert
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.launch
import org.controlsfx.validation.Severity
import org.controlsfx.validation.ValidationSupport
import org.controlsfx.validation.Validator
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Can't use `javafxx-coroutines` because by the time `consume`
 * is called in coroutine context, it is already too late.
 */
fun Node.onActionFilter(
    context: CoroutineContext = FX,
    action: suspend CoroutineScope.() -> Unit
) = addEventFilter(ACTION) {
    it.consume()
    launch(context) { action() }
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

inline fun <T> Control.validator(
    message: String,
    severity: Severity,
    required: Boolean = true,
    noinline predicate: (T) -> Boolean
): Boolean = ValidationSupport().registerValidator(this, required,
    Validator.createPredicateValidator<T>(predicate, message, severity))