package com.wijayaprinting.ui.employee

import com.wijayaprinting.R
import com.wijayaprinting.db.dao.Employee
import com.wijayaprinting.db.dao.Employee.Companion.DEFAULT_PASSWORD
import com.wijayaprinting.db.schema.Employees
import com.wijayaprinting.db.transaction
import com.wijayaprinting.ui.AddUserDialog
import com.wijayaprinting.ui.Controller
import com.wijayaprinting.ui.Refreshable
import com.wijayaprinting.util.tidy
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ButtonType.NO
import javafx.scene.control.ButtonType.YES
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.ChoiceBoxTableCell.forTableColumn
import kotfx.*
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDBSession
import kotlinx.nosql.update

class EmployeeController : Controller(), Refreshable {

    @FXML lateinit var fullAccessButton: Button
    @FXML lateinit var resetPasswordButton: Button
    @FXML lateinit var deleteButton: Button

    @FXML lateinit var employeeTable: TableView<Employee>
    @FXML lateinit var nameColumn: TableColumn<Employee, String>
    @FXML lateinit var fullAccessColumn: TableColumn<Employee, String>

    override fun initialize() {
        arrayOf(fullAccessButton, resetPasswordButton, deleteButton).forEach {
            it.disableProperty().bind(employeeTable.selectionModel.selectedItemProperty().isNull)
        }

        nameColumn.setCellValueFactory { it.value.name.toProperty() }
        fullAccessColumn.setCellValueFactory { getString(if (it.value.fullAccess) R.string.yes else R.string.no).toProperty() }
        fullAccessColumn.cellFactory = forTableColumn<Employee, String>(*getStringArray(R.string.yes, R.string.no))
        fullAccessColumn.setOnEditCommit { event ->
            val result = event.newValue == getString(R.string.yes)
            transaction { Employees.find { name.equal(event.rowValue.name) }.projection { fullAccess }.update(result) }
            event.rowValue.fullAccess = result
        }
        refresh()
    }

    override fun refresh() {
        employeeTable.items = transaction { Employees.find().toMutableObservableList() }
    }

    @FXML
    fun add() = AddUserDialog(this, getString(R.string.add_employee)).showAndWait().ifPresent { name ->
        val employee = Employee(name.tidy, DEFAULT_PASSWORD, false)
        employee.id = transaction { Employees.insert(employee) }!!
        employeeTable.items.add(employee)
        employeeTable.selectionModel.select(employee)
    }

    @FXML fun fullAccess() = confirm { employee -> Employees.find { name.equal(employee.name) }.projection { fullAccess }.update(!employee.fullAccess) }
    @FXML fun resetPassword() = confirm { employee -> Employees.find { name.equal(employee.name) }.projection { password }.update(DEFAULT_PASSWORD) }
    @FXML fun delete() = confirm { employee -> Employees.find { name.equal(employee.name) }.remove() }

    private fun confirm(confirmedAction: MongoDBSession.(Employee) -> Unit) = confirmAlert(getString(R.string.are_you_sure), YES, NO)
            .showAndWait()
            .filter { it == YES }
            .ifPresent {
                employeeTable.selectionModel.selectedItem.let { employee ->
                    transaction { confirmedAction(employee) }
                    when {
                        employee.name != employeeName -> refresh()
                        else -> infoAlert(getString(R.string.please_restart)).showAndWait().ifPresent { exit() }
                    }
                }
            }
}