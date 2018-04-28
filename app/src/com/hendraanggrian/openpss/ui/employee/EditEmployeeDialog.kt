package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.style
import javafx.scene.control.Dialog
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ktfx.layouts.button
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.layouts.tooltip
import ktfx.scene.control.closeButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.layout.gap

class EditEmployeeDialog(resourced: Resourced, employee: Employee) : Dialog<Employee>(), Resourced by resourced {

    private lateinit var nameField: TextField
    private lateinit var fullAccessImage: ImageView

    init {
        style()
        headerTitle = getString(R.string.edit_employee)
        graphicIcon = ImageView(R.image.header_employee)
        dialogPane.content = gridPane {
            gap = 8.0
            label(getString(R.string.name)) col 0 row 0
            nameField = textField(employee.name) col 1 row 0
            button(graphic = ImageView(Image(R.image.button_edit))) {
                tooltip(getString(R.string.edit_name))
            } col 2 row 0
            label(getString(R.string.full_access)) col 0 row 1
            /*fullAccessImage = imageView(Image(when {
                employee.fullAccess -> R.image.button_done_yes
                else -> R.image.button_done_no
            })) col 1 row 1*/
            button(graphic = ImageView(Image(R.image.button_edit))) {
                tooltip(getString(R.string.edit_name))
            } col 2 row 1
        }
        closeButton()
    }
}