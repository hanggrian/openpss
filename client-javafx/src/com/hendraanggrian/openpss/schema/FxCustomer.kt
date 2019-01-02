package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.data.Customer
import com.hendraanggrian.openpss.enumValueOfId
import com.hendraanggrian.openpss.id
import com.hendraanggrian.openpss.Resources2

fun Customer.Contact.Companion.new(type: ContactType, value: String): Customer.Contact =
    Customer.Contact(type.id, value)

inline val Customer.Contact.typedType: ContactType
    get() = enumValueOfId(
        type
    )

enum class ContactType : Resources2.Enum {
    PHONE {
        override val resourceId: String = R2.string.phone
    },
    EMAIL {
        override val resourceId: String = R2.string.email
    }
}