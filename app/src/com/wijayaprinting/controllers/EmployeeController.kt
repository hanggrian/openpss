package com.wijayaprinting.controllers

import com.wijayaprinting.base.Refreshable
import com.wijayaprinting.db.Employee
import com.wijayaprinting.db.Employees
import com.wijayaprinting.db.transaction
import javafx.fxml.FXML
import javafx.scene.control.ListView
import kotfx.toMutableObservableList

class EmployeeController : Controller(), Refreshable {

    @FXML lateinit var employeeList: ListView<Employee>

    @FXML
    override fun initialize() {
        refresh()
    }

    @FXML
    override fun refresh() {
        employeeList.items = transaction { Employees.find().toMutableObservableList() }
    }
}