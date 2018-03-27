package com.hendraanggrian.openpss.converters

import com.hendraanggrian.openpss.db.schema.Config
import com.hendraanggrian.openpss.db.schema.Configs
import com.hendraanggrian.openpss.db.transaction
import javafx.util.converter.CurrencyStringConverter
import kotlinx.nosql.equal
import java.util.Locale

class MoneyStringConverter : CurrencyStringConverter(transaction {
    val language = Configs.find { key.equal(Config.KEY_CURRENCY_LANGUAGE) }.singleOrNull()?.value
    val country = Configs.find { key.equal(Config.KEY_CURRENCY_COUNTRY) }.singleOrNull()?.value
    when {
        language != null && country != null -> Locale(language, country)
        else -> Locale.getDefault()
    }
})