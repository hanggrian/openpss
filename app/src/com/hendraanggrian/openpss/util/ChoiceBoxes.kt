@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import javafx.scene.control.ChoiceBox

@Suppress("UNCHECKED_CAST")
inline fun <T> ChoiceBox<*>.get(): T = value as T