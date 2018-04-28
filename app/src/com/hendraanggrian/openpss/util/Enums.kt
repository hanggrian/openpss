package com.hendraanggrian.openpss.util

/** This id will be used as NoSQL json key. */
inline val <E : Enum<E>> Enum<E>.id get() = toString().toLowerCase()

/** Find enum value from id. */
inline fun <reified E : Enum<E>> enumValueOfId(id: String) = enumValues<E>().single { it.id == id }