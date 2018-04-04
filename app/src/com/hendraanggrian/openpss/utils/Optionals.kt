@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.utils

import java.util.Optional

inline fun <T> Optional<T>.getNullable(): T? = orElse(null)