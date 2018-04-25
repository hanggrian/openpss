package com.hendraanggrian.openpss.db

import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Invoice.Offset.Technique.ONE_SIDE
import com.hendraanggrian.openpss.db.schemas.Invoice.Offset.Technique.TWO_SIDE_DISTINCT
import com.hendraanggrian.openpss.db.schemas.Invoice.Offset.Technique.TWO_SIDE_EQUAL

interface Order {

    val qty: Int

    val total: Double
}

interface SimpleOrder : Order {

    val price: Double

    override val total: Double get() = qty * price
}

interface OffsetOrder : Order {

    val typedTechnique: Invoice.Offset.Technique

    val minQty: Int

    val minPrice: Double

    val excessPrice: Double

    override val total: Double
        get() = when (typedTechnique) {
            ONE_SIDE -> calculateSide(qty, minQty, minPrice, excessPrice)
            TWO_SIDE_EQUAL -> calculateSide(qty * 2, minQty, minPrice, excessPrice)
            TWO_SIDE_DISTINCT -> calculateSide(qty, minQty, minPrice, excessPrice) * 2
        }

    private fun calculateSide(qty: Int, minQty: Int, minPrice: Double, excessPrice: Double) = when {
        qty <= minQty -> minPrice
        else -> minPrice + ((qty - minQty) * excessPrice)
    }
}