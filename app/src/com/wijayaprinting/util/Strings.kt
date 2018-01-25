package com.wijayaprinting.util

import java.lang.Character.isDigit

inline val String.tidy: String get() = replace(Regex("\\s+"), " ").trim()

inline val String.fullyCapitalize: String get() = split(" ").joinToString(" ") { it.capitalize() }

inline val String.withoutCurrency: String get() = substring(indexOf(toCharArray().first { isDigit(it) }))