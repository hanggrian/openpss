package com.hendraanggrian.openpss.converter

import com.hendraanggrian.openpss.db.schema.Config
import javafx.util.converter.CurrencyStringConverter

class MoneyStringConverter : CurrencyStringConverter(Config.getCurrencyLocale())