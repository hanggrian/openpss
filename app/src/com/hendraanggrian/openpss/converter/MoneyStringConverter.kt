package com.hendraanggrian.openpss.converter

import com.hendraanggrian.openpss.Language
import com.hendraanggrian.openpss.io.properties.ConfigFile
import javafx.util.converter.CurrencyStringConverter

class MoneyStringConverter : CurrencyStringConverter(Language.from(ConfigFile.language.value).asLocale())