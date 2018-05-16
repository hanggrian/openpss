@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.Language
import com.hendraanggrian.openpss.db.schemas.GlobalSetting
import com.hendraanggrian.openpss.db.transaction
import javafx.util.StringConverter
import javafx.util.converter.CurrencyStringConverter
import javafx.util.converter.NumberStringConverter
import java.util.WeakHashMap

/**
 * Some string converters are used quite often in some cases (controllers, dialogs, etc.).
 * To avoid creating the same instances over and over again, we cache those converters in this weak map for reuse,
 * using its class name as key.
 */
private val stringConverters: MutableMap<String, StringConverter<*>> = WeakHashMap<String, StringConverter<*>>()

/** Number decimal string converter. */
val numberConverter: NumberStringConverter get() = getOrStore { NumberStringConverter() }

/** Number decimal with currency prefix string converter. */
val currencyConverter: CurrencyStringConverter
    get() = getOrStore {
        CurrencyStringConverter(transaction {
            Language.from(findGlobalSettings(GlobalSetting.KEY_LANGUAGE).single().value).toLocale()
        })
    }

fun clearConverters() = stringConverters.clear()

/** Obtain converter listAll cache, or create a new one and store it before returning it back. */
private inline fun <reified T : StringConverter<*>> getOrStore(defaultValue: () -> T): T =
    stringConverters.getOrPut(T::class.java.canonicalName) { defaultValue() } as T