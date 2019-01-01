package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.data.Invoice
import com.hendraanggrian.openpss.enumValueOfId
import com.hendraanggrian.openpss.id
import com.hendraanggrian.openpss.ui.Resources

fun Invoice.Companion.no(resources: Resources, no: Number?): String? =
    no?.let { "${resources.getString(R.string.invoice)} #$it" }

fun Invoice.OffsetJob.Companion.new(
    qty: Int,
    title: String,
    total: Double,
    type: String,
    technique: Technique
): Invoice.OffsetJob = Invoice.OffsetJob(qty, title, total, type, technique.id)

inline val Invoice.OffsetJob.typedTechnique: Technique get() = enumValueOfId(technique)

enum class Technique : Resources.Enum {
    ONE_SIDE {
        override val resourceId: String = R.string.one_side
    },
    TWO_SIDE_EQUAL {
        override val resourceId: String = R.string.two_side_equal
    },
    TWO_SIDE_DISTINCT {
        override val resourceId: String = R.string.two_side_distinct
    }
}