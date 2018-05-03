package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.UserDialog
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employee.Role.MANAGER
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.util.doneCell
import com.hendraanggrian.openpss.util.stringCell
import com.hendraanggrian.openpss.util.yesNoAlert
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.SelectionModel
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import kotlinx.nosql.equal
import kotlinx.nosql.id
import kotlinx.nosql.update
import ktfx.application.later
import ktfx.collections.toMutableObservableList
import java.net.URL
import java.util.ResourceBundle

class EmployeeController : Controller(), Refreshable, Selectable<Employee> {

    @FXML lateinit var editButton: Button
    @FXML lateinit var deleteButton: Button
    @FXML lateinit var fullAccessButton: Button
    @FXML lateinit var resetPasswordButton: Button
    @FXML lateinit var employeeTable: TableView<Employee>
    @FXML lateinit var nameColumn: TableColumn<Employee, String>
    @FXML lateinit var fullAccessColumn: TableColumn<Employee, Boolean>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        editButton.disableProperty().bind(!selectedBinding)
        deleteButton.disableProperty().bind(!selectedBinding)
        fullAccessButton.disableProperty().bind(!selectedBinding)
        resetPasswordButton.disableProperty().bind(!selectedBinding)
        nameColumn.stringCell { name }
        later { fullAccessColumn.doneCell(128) { transaction { login.isAtLeast(MANAGER) } } }
    }

    override fun refresh() {
        employeeTable.items = transaction { Employees.find().toMutableObservableList() }
    }

    override val selectionModel: SelectionModel<Employee> get() = employeeTable.selectionModel

    @FXML fun add() = UserDialog(this, R.string.add_employee, R.image.header_employee).showAndWait().ifPresent {
        val employee = Employee.new(it)
        employee.id = transaction { Employees.insert(employee) }
        employeeTable.items.add(employee)
        selectionModel.select(employee)
    }

    @FXML fun edit() = EditEmployeeDialog(this, selected!!).showAndWait().ifPresent { employee ->
        transaction {
            Employees { it.id.equal(selected!!.id) }
                .projection { name + password + role }
                .update(employee.name, employee.password, employee.role)
        }
        refresh()
    }

    @FXML fun delete() = yesNoAlert {
        transaction { Employees[selected!!].remove() }
        refresh()
    }
}