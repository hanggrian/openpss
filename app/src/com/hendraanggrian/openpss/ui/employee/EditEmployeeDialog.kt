package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.getColor
import com.hendraanggrian.openpss.util.getFont
import com.hendraanggrian.openpss.util.getStyle
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ktfx.layouts.button
import ktfx.layouts.columnConstraints
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.tooltip
import ktfx.scene.control.closeButton
import ktfx.scene.control.customButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.layout.gap

class EditEmployeeDialog(resourced: Resourced, employee: Employee) : Dialog<Employee>(), Resourced by resourced {

    private lateinit var nameLabel: Label
    private lateinit var fullAccessLabel: Label

    init {
        headerTitle = getString(R.string.edit_employee)
        graphicIcon = ImageView(R.image.header_employee)
        dialogPane.run {
            stylesheets += getStyle(R.style.openpss)
            content = gridPane {
                columnConstraints {
                    constraints()
                    constraints(256.0)
                    constraints()
                }
                gap = 8.0
                label(getString(R.string.name)) col 0 row 0
                nameLabel = label(employee.name) {
                    font = getFont(R.font.sf_pro_text_bold)
                } col 1 row 0
                button(graphic = ImageView(Image(R.image.button_edit))) {
                    tooltip(getString(R.string.edit_name))
                } col 2 row 0
                label(getString(R.string.full_access)) col 0 row 1
                val isFullAccess = transaction { employee.isFullAccess() }
                fullAccessLabel = label(getString(if (isFullAccess) R.string.enabled else R.string.disabled)) {
                    font = getFont(R.font.sf_pro_text_bold)
                    textFill = getColor(if (isFullAccess) R.color.teal else R.color.red)
                } col 1 row 1
                button(graphic = ImageView(Image(R.image.button_edit))) {
                    tooltip(getString(R.string.edit_name))
                } col 2 row 1
            }
        }
        customButton(getString(R.string.reset_password))
        closeButton()
    }
}