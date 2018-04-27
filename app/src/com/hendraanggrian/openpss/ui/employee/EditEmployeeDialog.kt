package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.style
import javafx.scene.control.CheckBox
import javafx.scene.control.Dialog
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import ktfx.layouts.checkBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap

class EditEmployeeDialog(resourced: Resourced, employee: Employee) : Dialog<Employee>(), Resourced by resourced {

    private lateinit var nameField: TextField
    private lateinit var fullAccessCheck: CheckBox

    init {
        style()
        headerTitle = getString(R.string.edit_employee)
        graphicIcon = ImageView(R.image.header_employee)
        dialogPane.content = gridPane {
            gap = 8.0
            label(getString(R.string.name)) col 0 row 0
            nameField = textField(employee.name) col 1 row 0
            label(getString(R.string.full_access)) col 0 row 1
            fullAccessCheck = checkBox { isSelected = employee.fullAccess } col 1 row 1
        }
        cancelButton()
        okButton()
    }
}