package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employee.Companion.DEFAULT_PASSWORD
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.controls.UserDialog
import com.hendraanggrian.openpss.ui.main.ResetPasswordDialog
import com.hendraanggrian.openpss.utils.doneCell
import com.hendraanggrian.openpss.utils.stringCell
import com.hendraanggrian.openpss.utils.yesNoAlert
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.SelectionModel
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDBSession
import kotlinx.nosql.update
import ktfx.application.exit
import ktfx.collections.toMutableObservableList
import ktfx.scene.control.infoAlert
import java.net.URL
import java.util.ResourceBundle

class EmployeeController : Controller(), Refreshable, Selectable<Employee> {

    @FXML lateinit var fullAccessButton: Button
    @FXML lateinit var resetPasswordButton: Button
    @FXML lateinit var deleteButton: Button

    @FXML lateinit var employeeTable: TableView<Employee>
    @FXML lateinit var nameColumn: TableColumn<Employee, String>
    @FXML lateinit var fullAccessColumn: TableColumn<Employee, Boolean>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        fullAccessButton.disableProperty().bind(!selectedBinding)
        resetPasswordButton.disableProperty().bind(!selectedBinding)
        deleteButton.disableProperty().bind(!selectedBinding)
        nameColumn.stringCell { name }
        fullAccessColumn.doneCell(128) { fullAccess }
    }

    override fun refresh() {
        employeeTable.items = transaction { Employees.find().toMutableObservableList() }
    }

    override val selectionModel: SelectionModel<Employee> get() = employeeTable.selectionModel

    @FXML fun addEmployee() = UserDialog(this, R.string.add_employee, R.image.ic_employee).showAndWait().ifPresent {
        val employee = Employee.new(it)
        employee.id = transaction { Employees.insert(employee) }!!
        employeeTable.items.add(employee)
        employeeTable.selectionModel.select(employee)
    }

    @FXML fun fullAccess() = confirm({ employee ->
        Employees.find { name.equal(employee.name) }.projection { fullAccess }.update(!employee.fullAccess)
    })

    @FXML fun resetPassword() = confirm({ employee ->
        Employees.find { name.equal(employee.name) }.projection { password }.update(DEFAULT_PASSWORD)
    }) {
        ResetPasswordDialog(this).showAndWait().ifPresent { newPassword ->
            transaction {
                Employees.find { name.equal(employeeName) }.projection { password }.update(newPassword)
                infoAlert(getString(R.string.change_password_successful)).show()
            }
        }
    }

    @FXML fun delete() = confirm({ employee ->
        Employees.find { name.equal(employee.name) }.remove()
    })

    private fun confirm(
        confirmedAction: MongoDBSession.(Employee) -> Unit,
        isNotSelfAction: () -> Unit = {
            infoAlert(getString(R.string.please_restart)).showAndWait().ifPresent { exit() }
        }
    ) = yesNoAlert {
        selected!!.let {
            transaction { confirmedAction(it) }
            when {
                it.name != employeeName -> refresh()
                else -> isNotSelfAction()
            }
        }
    }
}