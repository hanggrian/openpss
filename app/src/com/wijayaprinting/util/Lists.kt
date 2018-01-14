@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.util

inline infix fun <E> List<E>.with(element: E): List<E> = toMutableList().apply { add(element) }