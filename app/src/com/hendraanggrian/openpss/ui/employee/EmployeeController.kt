package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.UserDialog
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employee.Companion.DEFAULT_PASSWORD
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.main.ChangePasswordDialog
import com.hendraanggrian.openpss.util.doneCell
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.stringCell
import com.hendraanggrian.openpss.util.yesNoAlert
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.SelectionModel
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.application.exit
import ktfx.application.later
import ktfx.collections.toMutableObservableList
import ktfx.scene.control.styledInfoAlert
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
        later { fullAccessColumn.doneCell(128) { transaction { login.isFullAccess() } } }
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

    @FXML fun edit() = EditEmployeeDialog(this, login).showAndWait().ifPresent {
    }

    @FXML fun delete() = confirm({ employee ->
        Employees { it.name.equal(employee.name) }.remove()
    })

    @FXML fun fullAccess() = confirm({ employee ->
        // Employees { name.equal(employee.name) }.projection { fullAccess }.update(!employee.fullAccess)
    })

    @FXML fun resetPassword() = confirm({ employee ->
        Employees { it.name.equal(employee.name) }.projection { password }.update(DEFAULT_PASSWORD)
    }) {
        ChangePasswordDialog(this).showAndWait().ifPresent { newPassword ->
            transaction {
                // Employees { name.equal(employeeName) }.projection { password }.update(newPassword)
                styledInfoAlert(getStyle(R.style.openpss), getString(R.string.successfully_changed_password)).show()
            }
        }
    }

    private fun confirm(
        confirmedAction: SessionWrapper.(Employee) -> Unit,
        isNotSelfAction: () -> Unit = {
            styledInfoAlert(getStyle(R.style.openpss), getString(R.string.please_restart)).showAndWait()
                .ifPresent { exit() }
        }
    ) = yesNoAlert {
        selected!!.let {
            transaction { confirmedAction(it) }
            when {
                it.name != login.name -> refresh()
                else -> isNotSelfAction()
            }
        }
    }
}