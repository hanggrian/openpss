package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Resources
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.content.enumValueOfId
import com.hendraanggrian.openpss.content.id

fun Customer.Contact.Companion.new(type: ContactType, value: String): Customer.Contact =
    Customer.Contact(type.id, value)

inline val Customer.Contact.typedType: ContactType get() = enumValueOfId(type)

enum class ContactType : Resources.Enum {
    PHONE {
        override val resourceId: String = R.string.phone
    },
    EMAIL {
        override val resourceId: String = R.string.email
    }
}