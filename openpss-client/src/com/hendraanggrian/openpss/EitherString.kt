package com.hendraanggrian.openpss

import arrow.core.Either

typealias EitherString<V> = Either<String, V>

inline fun <V> EitherString<V>.foldError(
    onError: (String) -> Unit,
    onSuccess: (V) -> Unit
): Unit = fold(onError, onSuccess)
