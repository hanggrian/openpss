package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.UserDialog
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employee.Role.EXECUTIVE
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.util.stringCell
import com.hendraanggrian.openpss.util.yesNoAlert
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.SelectionModel
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.nosql.equal
import kotlinx.nosql.id
import kotlinx.nosql.update
import ktfx.beans.property.toReadOnlyProperty
import ktfx.beans.value.or
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.FX
import java.net.URL
import java.util.ResourceBundle

class EmployeeController : Controller(), Refreshable, Selectable<Employee> {

    @FXML lateinit var editButton: Button
    @FXML lateinit var deleteButton: Button
    @FXML lateinit var employeeTable: TableView<Employee>
    @FXML lateinit var nameColumn: TableColumn<Employee, String>
    @FXML lateinit var roleColumn: TableColumn<Employee, String>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        launch(FX) {
            delay(100)
            transaction { login.isAtLeast(EXECUTIVE) }.toReadOnlyProperty().let {
                editButton.disableProperty().bind(!selectedBinding or !it)
                deleteButton.disableProperty().bind(!selectedBinding or !it)
            }
        }
        nameColumn.stringCell { name }
        roleColumn.stringCell { typedRole.toString() }
    }

    override fun refresh() {
        employeeTable.items = transaction { Employees().toMutableObservableList() }
    }

    override val selectionModel: SelectionModel<Employee> get() = employeeTable.selectionModel

    @FXML fun add() = UserDialog(this, R.string.add_employee, R.image.header_employee, restrictiveInput = false)
        .showAndWait().ifPresent {
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
        transaction { Employees -= selected!! }
        refresh()
    }
}