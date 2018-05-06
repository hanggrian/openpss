package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.UserPopup
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
import javafx.scene.image.ImageView
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.nosql.update
import ktfx.beans.property.toReadOnlyProperty
import ktfx.beans.value.or
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.FX
import ktfx.coroutines.onAction
import ktfx.layouts.button
import ktfx.layouts.separator
import ktfx.layouts.tooltip
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

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        refreshButton = button(graphic = ImageView(R.image.btn_refresh)) {
            tooltip(getString(R.string.refresh))
            onAction { refresh() }
        }
        addButton = button(graphic = ImageView(R.image.btn_add)) {
            tooltip(getString(R.string.add_employee))
            onAction { add() }
        }
        editButton = button(graphic = ImageView(R.image.btn_edit)) {
            tooltip(getString(R.string.edit_employee))
            onAction { edit() }
        }
        deleteButton = button(graphic = ImageView(R.image.btn_delete)) {
            tooltip(getString(R.string.delete))
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

    override val selectionModel: SelectionModel<Employee> get() = employeeTable.selectionModel

    private fun add() = UserPopup(this, R.string.add_employee, false).show(addButton) {
        val employee = Employee.new(it)
        employee.id = transaction { Employees.insert(employee) }
        employeeTable.items.add(employee)
        selectionModel.select(employee)
    }

    private fun edit() = EditEmployeePopup(this, selected!!).show(editButton) {
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