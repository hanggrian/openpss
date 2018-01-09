package com.wijayaprinting.data

import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
import kotfx.asMutableProperty
import kotfx.bind
import kotfx.bindingOf
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

data class ProductOrder<out T> @JvmOverloads constructor(
        val product: T,
        val qty: IntegerProperty = SimpleIntegerProperty(),
        val price: ObjectProperty<BigDecimal> = ZERO.asMutableProperty(),
        val total: ObjectProperty<BigDecimal> = ZERO.asMutableProperty()
) {

    init {
        total bind bindingOf(qty, price) { BigDecimal(qty.value) * price.value }
    }
}