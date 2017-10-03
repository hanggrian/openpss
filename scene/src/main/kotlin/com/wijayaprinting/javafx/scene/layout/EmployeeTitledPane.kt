package com.wijayaprinting.javafx.scene.layout

import com.wijayaprinting.javafx.R
import com.wijayaprinting.javafx.scene.control.EmployeeListView
import com.wijayaprinting.javafx.data.Employee
import com.wijayaprinting.javafx.getString
import com.wijayaprinting.javafx.scene.control.IntField
import com.wijayaprinting.javafx.scene.control.TimeField
import com.wijayaprinting.javafx.scene.utils.setGap
import com.wijayaprinting.mysql.dao.Shift
import javafx.beans.property.Property
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import kotfx.bindings.not
import kotfx.bindings.or
import kotfx.runLater
import kotfx.stringConverter
import org.joda.time.DateTime
import java.util.*

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class EmployeeTitledPane(title: String, shifts: ObservableList<Shift>, private val listView: EmployeeListView) : TitledPane(title, VBox()) {

    private val choiceBox: ChoiceBox<Shift> = ChoiceBox<Shift>(shifts).apply {
        maxWidth = Double.MAX_VALUE
        valueProperty().bindBidirectional(employee.shift as Property<Shift>)
    }
    private val dailyField: TextField = IntField().apply {
        promptText = getString(R.string.daily_income)
        textProperty().bindBidirectional(employee.daily, stringConverter<Number> { if (it.isBlank()) 0 else Integer.valueOf(it) })
    }
    private val overtimeField: TextField = IntField().apply {
        promptText = getString(R.string.overtime_income)
        textProperty().bindBidirectional(employee.overtimeHourly, stringConverter<Number> { if (it.isBlank()) 0 else Integer.valueOf(it) })
    }
    private val addButton: Button = Button().apply {
        graphic = ImageView(Image(R.png.btn_add))
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
        contextMenu = ContextMenu(
                MenuItem(getString(R.string.add)),
                MenuItem(getString(R.string.delete)),
                SeparatorMenuItem(),
                MenuItem("${getString(R.string.delete)} ${employee.name}").apply {
                    setOnAction { (parent as Pane).children.remove(this@EmployeeTitledPane) }
                },
                MenuItem(getString(R.string.delete_others)).apply {
                    setOnAction { (parent as Pane).children.remove(this@EmployeeTitledPane) }
                })
    }

    class DateTimeDialog : Dialog<DateTime>() {

        val content = Content()

        init {
            title = getString(R.string.record)
            graphic = ImageView(R.png.ic_record)
            headerText = getString(R.string.record)

            dialogPane.content = content
            dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
            dialogPane.lookupButton(ButtonType.OK).disableProperty().bind(content.datePicker.valueProperty().isNull or not(content.timeField.validProperty))
            runLater { content.datePicker.requestFocus() }
            setResultConverter {
                when (it) {
                    ButtonType.OK -> DateTime(content.datePicker.value.year, content.datePicker.value.monthValue, content.datePicker.value.dayOfMonth, content.timeField.value!!.hourOfDay, content.timeField.value!!.minuteOfHour)
                    else -> null
                }
            }
        }

        inner class Content : GridPane() {
            val dateLabel = Label(getString(R.string.date))
            val datePicker: DatePicker = DatePicker().apply {
                isEditable = false // force pick from popup
                maxWidth = 128.0
                alignment = Pos.CENTER
            }
            val timeLabel = Label(getString(R.string.time))
            val timeField: TimeField = TimeField().apply {
                maxWidth = 64.0
                alignment = Pos.CENTER
            }

            init {
                setGap(8.0)
                add(dateLabel, 0, 0)
                add(datePicker, 1, 0)
                add(timeLabel, 0, 1)
                add(timeField, 1, 1)
            }
        }
    }
}