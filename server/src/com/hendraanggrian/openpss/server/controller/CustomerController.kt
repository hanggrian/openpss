package com.hendraanggrian.openpss.server.controller

import com.hendraanggrian.openpss.api.Page
import com.hendraanggrian.openpss.db.matches
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.server.db.transaction
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.regex.Pattern
import kotlin.math.ceil

@Suppress("unused")
interface CustomerController {

    @GetMapping("/customers")
    fun getCustomers(
        @RequestParam(value = "search", defaultValue = "") search: String,
        @RequestParam(value = "page") page: Int,
        @RequestParam(value = "count") count: Int
    ): Page<Customer> = transaction {
        val customers = Customers.buildQuery {
            if (search.isNotBlank()) {
                or(it.name.matches(search, Pattern.CASE_INSENSITIVE))
                or(it.address.matches(search, Pattern.CASE_INSENSITIVE))
                or(it.note.matches(search, Pattern.CASE_INSENSITIVE))
            }
        }
        Page(
            ceil(customers.count() / count.toDouble()).toInt(),
            customers.skip(count * page).take(count).toList()
        )
    }
}