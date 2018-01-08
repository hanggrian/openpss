package com.wijayaprinting.internal

import org.jetbrains.exposed.dao.IdTable

/** [IdTable] where primary key can be any type. */
abstract class CustomIdTable<T : Any> @JvmOverloads constructor(name: String = "") : IdTable<T>(name)