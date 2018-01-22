package com.wijayaprinting.db.dao

import com.wijayaprinting.db.schema.Customers
import com.wijayaprinting.db.Named
import javafx.collections.ObservableList
import kotfx.observableListOf
import kotlinx.nosql.Id
import org.joda.time.LocalDate

data class Customer @JvmOverloads constructor(
        override val name: String,
        var note: String = "",
        var since: LocalDate = LocalDate.now(),
        var contacts: List<Contact> = listOf()
) : Named<Customers> {
    override lateinit var id: Id<String, Customers>

    data class Contact(
            var type: String,
            var value: String
    )

    override fun toString(): String = name

    companion object {
        private const val TYPE_EMAIL = "email"
        private const val TYPE_PHONE = "phone"

        fun listAllTypes(): ObservableList<String> = observableListOf(TYPE_EMAIL, TYPE_PHONE)
    }
}