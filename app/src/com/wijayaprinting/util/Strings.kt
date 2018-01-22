package com.wijayaprinting.util

inline val String.tidied get() = replace(Regex("\\s+"), " ").trim()