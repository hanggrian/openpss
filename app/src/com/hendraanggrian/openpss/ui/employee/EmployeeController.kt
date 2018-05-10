package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.App.Companion.STYLE_DEFAULT_BUTTON
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.UserPopup
import com.hendraanggrian.openpss.controls.stretchableButton
import com.hendraanggrian.openpss.controls.styledStretchableButton
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employee.Role.EXECUTIVE
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.SegmentedController
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.util.stringCell
import com.hendraanggrian.openpss.util.yesNoAlert
import javafx.fxml.FXML
import javafx.geometry.Orientation.VERTICAL
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.SelectionModel
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.nosql.update
import ktfx.beans.property.toReadOnlyProperty
import ktfx.beans.value.or
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.FX
import ktfx.coroutines.onAction
import ktfx.layouts.separator
import java.net.URL
import java.util.ResourceBundle

class EmployeeController : SegmentedController(), Refreshable, Selectable<Employee> {

    @FXML lateinit var employeeTable: TableView<Employee>
    @FXML lateinit var nameColumn: TableColumn<Employee, String>
    @FXML lateinit var roleColumn: TableColumn<Employee, String>

    private lateinit var refreshButton: Button
    private lateinit var addButton: Button
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    override val leftButtons: List<Node>
        get() = listOf(refreshButton, separator(VERTICAL), addButton, editButton, deleteButton)

    override val selectionModel: SelectionModel<Employee> get() = employeeTable.selectionModel

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        refreshButton = stretchableButton(getString(R.string.refresh), R.image.btn_refresh_light) {
            onAction { refresh() }
        }
        addButton = styledStretchableButton(STYLE_DEFAULT_BUTTON, getString(R.string.add), R.image.btn_add_dark) {
            onAction { add() }
        }
        editButton = stretchableButton(getString(R.string.edit), R.image.btn_edit_light) {
            onAction { edit() }
        }
        deleteButton = stretchableButton(getString(R.string.delete), R.image.btn_delete_light) {
            onAction { delete() }
        }
        launch(FX) {
            delay(100)
            transaction { login.isAtLeast(EXECUTIVE) }.toReadOnlyProperty().let {
                addButton.disableProperty().bind(!it)
                editButton.disableProperty().bind(!selectedBinding or !it)
                deleteButton.disableProperty().bind(!selectedBinding or !it)
            }
        }
        nameColumn.stringCell { name }
        roleColumn.stringCell { typedRole.toString() }
    }

    override fun refresh() = employeeTable.items.let {
        it.clear()
        it += transaction { Employees().toMutableObservableList().also { it -= Employee.BACKDOOR } }
    }

    private fun add() = UserPopup(this, R.string.add_employee, false).showAt(addButton) {
        val employee = Employee.new(it)
        employee.id = transaction { Employees.insert(employee) }
        employeeTable.items.add(employee)
        selectionModel.select(employee)
    }

    private fun edit() = EditEmployeePopup(this, selected!!).showAt(editButton) {
        transaction {
            Employees[selected!!.id]
                .projection { name + password + role }
                .update(it.name, it.password, it.role)
        }
        refresh()
    }

    private fun delete() = yesNoAlert {
        transaction { Employees -= selected!! }
        refresh()
    }
}