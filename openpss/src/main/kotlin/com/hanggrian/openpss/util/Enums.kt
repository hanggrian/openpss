package com.hanggrian.openpss.util

/** This id will be used as NoSQL json key. */
inline val Enum<*>.id: String get() = name.lowercase()

/** Find enum value from id. */
inline fun <reified E : Enum<E>> enumValueOfId(id: String): E =
    enumValues<E>().single { it.id == id }
