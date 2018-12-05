package com.hendraanggrian.openpss.content

/** This id will be used as NoSQL json key. */
inline val Enum<*>.id: String get() = name.toLowerCase()

/** Find enum value from id. */
inline fun <reified E : Enum<E>> enumValueOfId(id: String): E = enumValues<E>().single { it.id == id }