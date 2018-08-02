@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import javafx.scene.control.Control
import org.controlsfx.validation.Severity
import org.controlsfx.validation.ValidationSupport
import org.controlsfx.validation.Validator

inline fun <T> Control.validator(
    message: String,
    severity: Severity,
    required: Boolean = true,
    noinline predicate: (T) -> Boolean
) = ValidationSupport().registerValidator(this, required,
    Validator.createPredicateValidator<T>(predicate, message, severity))