@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.util

/** Creates a copy of this list with [element] added. */
inline operator fun <E> List<E>.plus(element: E): List<E> = toMutableList().apply { add(element) }

/** Creates a copy of this list with [element] removed. */
inline operator fun <E> List<E>.minus(element: E): List<E> = toMutableList().apply { remove(element) }