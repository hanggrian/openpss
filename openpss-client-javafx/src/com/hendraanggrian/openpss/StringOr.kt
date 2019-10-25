package com.hendraanggrian.openpss

import ktfx.dialogs.errorAlert

inline fun <V> StringOr<V>.foldString(onSuccess: (V) -> Unit): Unit =
    fold({ errorAlert(content = it) }, onSuccess)
