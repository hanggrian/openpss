package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.StringResources
import com.hendraanggrian.openpss.enumValueOfId
import com.hendraanggrian.openpss.id

fun Customer.Contact.Companion.new(type: ContactType, value: String): Customer.Contact =
    Customer.Contact(type.id, value)

inline val Customer.Contact.typedType: ContactType
    get() = enumValueOfId(
        type
    )

enum class ContactType : StringResources.Enum {
    PHONE {
        override val resourceId: String = R2.string.phone
    },
    EMAIL {
        override val resourceId: String = R2.string.email
    }
}
