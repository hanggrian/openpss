package com.hendraanggrian.openpss.converter

import javafx.util.converter.CurrencyStringConverter
import java.text.DecimalFormat
import java.text.NumberFormat.getCurrencyInstance

class MoneyStringConverter : CurrencyStringConverter(getCurrencyInstance().apply {
    val decimalFormat = this as DecimalFormat
    decimalFormat.decimalFormatSymbols = decimalFormat.decimalFormatSymbols.apply { currencySymbol = "" }
})