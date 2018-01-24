package com.wijayaprinting.util

import java.lang.Character.isDigit

inline val String.tidy get() = replace(Regex("\\s+"), " ").trim()

inline val String.withoutCurrency get() = substring(indexOf(toCharArray().first { isDigit(it) }))