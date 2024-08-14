package com.hanggrian.openpss.util

inline fun <T> Iterable<T>.isEmpty(): Boolean = count() == 0

inline fun <T> Iterable<T>.isNotEmpty(): Boolean = count() != 0
