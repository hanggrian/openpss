package com.hendraanggrian.openpss.content

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
private val stringConverters: MutableMap<String, StringConverter<Number>> =
    WeakHashMap<String, StringConverter<Number>>()

/** Number decimal string converter. */
val numberConverter: StringConverter<Number>
    get() = stringConverters.getOrPut("number") { NumberStringConverter() }

/** Number decimal with currency prefix string converter. */
val currencyConverter: StringConverter<Number>
    get() = stringConverters.getOrPut("currency") {
        CurrencyStringConverter(transaction {
            Language.ofFullCode(findGlobalSettings(GlobalSetting.KEY_LANGUAGE).single().value).toLocale()
        })
    }

fun clearConverters() = stringConverters.clear()