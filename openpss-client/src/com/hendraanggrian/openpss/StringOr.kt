package com.hendraanggrian.openpss

import arrow.core.Either

typealias StringOr<V> = Either<String, V>

inline fun <V> StringOr<V>.foldString(
    onError: (String) -> Unit,
    onSuccess: (V) -> Unit
): Unit = fold(onError, onSuccess)
