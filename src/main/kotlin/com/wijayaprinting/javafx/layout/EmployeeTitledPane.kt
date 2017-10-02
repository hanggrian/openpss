package com.wijayaprinting.javafx.layout

import com.wijayaprinting.javafx.R
import com.wijayaprinting.javafx.control.EmployeeListView
import com.wijayaprinting.javafx.control.button.ImageButton
import com.wijayaprinting.javafx.control.field.IntField
import com.wijayaprinting.javafx.control.field.TextField
import com.wijayaprinting.javafx.data.Employee
import com.wijayaprinting.javafx.dialog.DateTimeDialog
import com.wijayaprinting.javafx.utils.getString
import com.wijayaprinting.mysql.dao.Shift
import javafx.beans.property.Property
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import kotfx.stringConverter
import java.util.*

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class EmployeeTitledPane(title: String, shifts: ObservableList<Shift>, private val listView: EmployeeListView) : TitledPane(title, VBox()) {

    private val choiceBox: ChoiceBox<Shift> = ChoiceBox<Shift>(shifts).apply {
        maxWidth = Double.MAX_VALUE
        valueProperty().bindBidirectional(employee.shift as Property<Shift>)
    }
    private val dailyField: TextField = IntField(getString(R.string.daily_income)).apply {
        textProperty().bindBidirectional(employee.daily, stringConverter<Number> { if (it.isBlank()) 0 else Integer.valueOf(it) })
    }
    private val overtimeField: TextField = IntField(getString(R.string.overtime_income)).apply {
        textProperty().bindBidirectional(employee.overtimeHourly, stringConverter<Number> { if (it.isBlank()) 0 else Integer.valueOf(it) })
    }
    private val addButton: Button = ImageButton(R.png.btn_add).apply {
        setOnAction {
            DateTimeDialog()
                    .showAndWait()
                    .ifPresent {
                        listView.items.add(it)
                        Collections.sort(listView.items)
                    }
        }
    }

    val employee: Employee get() = listView.employee

    init {
        (content as VBox).let {
            it.padding = Insets(-0.1) // force no padding
            it.children.add(choiceBox)
            it.children.add(dailyField)
            it.children.add(overtimeField)
            it.children.add(AnchorPane().apply {
                children.add(listView)
                AnchorPane.setTopAnchor(listView, 0.0)
                AnchorPane.setRightAnchor(listView, 0.0)
                AnchorPane.setBottomAnchor(listView, 0.0)
                AnchorPane.setLeftAnchor(listView, 0.0)
                children.add(addButton)
                AnchorPane.setTopAnchor(addButton, 0.0)
                AnchorPane.setRightAnchor(addButton, 0.0)
            })
        }

        isCollapsible = false
        contextMenu = ContextMenu()
        contextMenu.items.add(MenuItem(getString(R.string.delete)).apply {
            setOnAction {
                (parent as Pane).children.remove(this@EmployeeTitledPane)
            }
        })
    }
}