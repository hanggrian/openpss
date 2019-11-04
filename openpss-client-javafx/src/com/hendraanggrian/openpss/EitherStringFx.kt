package com.hendraanggrian.openpss

import ktfx.dialogs.errorAlert

inline fun <V> EitherString<V>.foldError(
    onError: (String) -> Unit = { errorAlert(content = it) },
    onSuccess: (V) -> Unit
): Unit = fold(onError, onSuccess)

inline fun <V> EitherString<V>.foldErrorAlert(
    crossinline onErrorPresent: () -> Unit,
    onSuccess: (V) -> Unit
): Unit = foldError({ errorAlert(content = it).ifPresent { onErrorPresent() } }, onSuccess)
