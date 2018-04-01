@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.db.schema.Config
import com.hendraanggrian.openpss.db.schema.Configs
import com.hendraanggrian.openpss.db.transaction
import javafx.util.StringConverter
import javafx.util.converter.CurrencyStringConverter
import javafx.util.converter.NumberStringConverter
import kotlinx.nosql.equal
import java.util.Locale
import java.util.WeakHashMap

/**
 * Some string converters are used quite often in some cases (controllers, dialogs, etc.).
 * To avoid creating the same instances over and over again, we cache those converters in this weak map for reuse,
 * using its class name as key.
 */
private val stringConverters = WeakHashMap<String, StringConverter<*>>()

/** Number decimal string converter. */
val numberConverter: NumberStringConverter get() = getOrStore { NumberStringConverter() }

/** Number decimal with currency prefix string converter. */
val currencyConverter: CurrencyStringConverter
    get() = getOrStore {
        CurrencyStringConverter(transaction {
            val language = Configs.find { key.equal(Config.KEY_CURRENCY_LANGUAGE) }.singleOrNull()?.value
            val country = Configs.find { key.equal(Config.KEY_CURRENCY_COUNTRY) }.singleOrNull()?.value
            when {
                language != null && country != null -> Locale(language, country)
                else -> Locale.getDefault()
            }
        })
    }

/** Obtain converter from cache, or create a new one and store it before returning it back. */
private inline fun <reified T : StringConverter<*>> getOrStore(creator: () -> T): T = T::class.java.canonicalName.let {
    @Suppress("UNCHECKED_CAST")
    var converter: T? = stringConverters[it] as? T
    if (converter == null) {
        converter = creator()
        stringConverters[it] = converter
    }
    return converter
}