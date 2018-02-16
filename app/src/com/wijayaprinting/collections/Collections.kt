@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.collections

inline fun <T> Iterable<T>.contains(predicate: (T) -> Boolean): Boolean = singleOrNull(predicate) != null

inline val <T> Iterable<T>.isEmpty: Boolean get() = count() == 0

inline val <T> Iterable<T>.isNotEmpty: Boolean get() = count() != 0