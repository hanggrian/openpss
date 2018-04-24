package com.hendraanggrian.openpss.util

inline val <E : Enum<E>> Enum<E>.id get() = toString().toLowerCase()

inline fun <reified E : Enum<E>> enumValueOfId(id: String) = enumValues<E>().single { it.id == id }