package com.hendraanggrian.openpss.action

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.localization.Resourced
import com.hendraanggrian.openpss.util.isNotEmpty
import com.hendraanggrian.openpss.util.matches
import javafx.event.ActionEvent
import ktfx.scene.control.styledErrorAlert
import java.util.regex.Pattern

class AddCustomerAction(
    resourced: Resourced,
    private val name: String
) : SimpleAction(resourced, R.string.add_customer) {

    override fun onAction(event: ActionEvent): Boolean {
        transaction {
            when {
                Customers { it.name.matches("^$it$", Pattern.CASE_INSENSITIVE) }.isNotEmpty() ->
                    styledErrorAlert(com.hendraanggrian.openpss.util.getStyle(R.style.openpss), getString(R.string.name_taken)).show()
                else -> Customer.new(name).let {
                    it.id = Customers.insert(it)
                    customerList.items.add(it)
                    customerList.selectionModel.select(customerList.items.lastIndex)
                }
            }
        }
    }
}