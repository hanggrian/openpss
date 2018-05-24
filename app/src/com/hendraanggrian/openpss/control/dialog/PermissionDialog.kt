package com.hendraanggrian.openpss.control.dialog

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.PasswordBox
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.localization.Resourced
import javafx.scene.control.ChoiceBox
import ktfx.layouts.vbox

class PermissionDialog(resourced: Resourced) : ResultableDialog<Boolean>(
    resourced,
    R.string.permission_required,
    R.image.header_change_password) {

    private lateinit var employeeChoice: ChoiceBox<Employee>
    private lateinit var passwordBox: PasswordBox

    init {
        vbox(8.0) {
            /*employeeChoice = choiceBox(transaction { Employees {it.role} }) {
            }*/
            passwordBox = PasswordBox(this@PermissionDialog).add()
        }
    }

    override val optionalResult: Boolean?
        get() = true
}