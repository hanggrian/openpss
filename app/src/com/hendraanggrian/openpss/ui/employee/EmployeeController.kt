package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schema.Employee
import com.hendraanggrian.openpss.db.schema.Employee.Companion.DEFAULT_PASSWORD
import com.hendraanggrian.openpss.db.schema.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.AddUserDialog
import com.hendraanggrian.openpss.ui.Addable
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.main.ResetPasswordDialog
import com.hendraanggrian.openpss.util.tidy
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ButtonType.NO
import javafx.scene.control.ButtonType.YES
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDBSession
import kotlinx.nosql.update
import ktfx.application.exit
import ktfx.beans.property.toProperty
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.FX
import ktfx.coroutines.onEditCommit
import ktfx.scene.control.choiceBoxCellFactory
import ktfx.scene.control.confirmAlert
import ktfx.scene.control.infoAlert
import java.net.URL
import java.util.ResourceBundle

class EmployeeController : Controller(), Refreshable, Addable {

    @FXML lateinit var fullAccessButton: Button
    @FXML lateinit var resetPasswordButton: Button
    @FXML lateinit var deleteButton: Button
    @FXML lateinit var configButton: Button

    @FXML lateinit var employeeTable: TableView<Employee>
    @FXML lateinit var nameColumn: TableColumn<Employee, String>
    @FXML lateinit var fullAccessColumn: TableColumn<Employee, String>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        arrayOf(fullAccessButton, resetPasswordButton, deleteButton).forEach {
            it.disableProperty().bind(employeeTable.selectionModel.selectedItemProperty().isNull)
        }
        launch(FX) {
            delay(500)
            configButton.isDisable = !isFullAccess
        }

        nameColumn.setCellValueFactory { it.value.name.toProperty() }
        fullAccessColumn.setCellValueFactory {
            getString(if (it.value.fullAccess) R.string.yes else R.string.no).toProperty()
        }
        fullAccessColumn.choiceBoxCellFactory(*getStringArray(R.string.yes, R.string.no))
        fullAccessColumn.onEditCommit { event ->
            val result = event.newValue == getString(R.string.yes)
            transaction { Employees.find { name.equal(event.rowValue.name) }.projection { fullAccess }.update(result) }
            event.rowValue.fullAccess = result
        }
        refresh()
    }

    override fun refresh() {
        employeeTable.items = transaction { Employees.find().toMutableObservableList() }
    }

    override fun add() = AddUserDialog(this, getString(R.string.add_employee)).showAndWait().ifPresent { name ->
        val employee = Employee.new(name.tidy())
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
                infoAlert(getString(R.string.change_password_successful)).showAndWait()
            }
        }
    }

    @FXML fun delete() = confirm({ employee ->
        Employees.find { name.equal(employee.name) }.remove()
    })

    @FXML fun config() = ConfigDialog(this).showAndWait().get()

    private fun confirm(
        confirmedAction: MongoDBSession.(Employee) -> Unit,
        isNotSelfAction: () -> Unit = {
            infoAlert(getString(R.string.please_restart)).showAndWait().ifPresent {
                exit()
            }
        }
    ) = confirmAlert(getString(R.string.are_you_sure), YES, NO)
        .showAndWait()
        .filter { it == YES }
        .ifPresent {
            employeeTable.selectionModel.selectedItem.let { employee ->
                transaction { confirmedAction(employee) }
                when {
                    employee.name != employeeName -> refresh()
                    else -> isNotSelfAction()
                }
            }
        }
}