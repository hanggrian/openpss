package com.wijayaprinting.javafx.control

import com.sun.javafx.scene.control.skin.LabeledText
import com.wijayaprinting.javafx.BuildConfig
import com.wijayaprinting.javafx.R
import com.wijayaprinting.javafx.data.Employee
import com.wijayaprinting.javafx.utils.getString
import com.wijayaprinting.mysql.utils.PATTERN_DATETIME
import javafx.scene.control.ContextMenu
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.input.MouseButton
import javafx.scene.layout.GridPane
import kotfx.controls.isNotSelected
import kotfx.controls.isSelected
import kotfx.dialogs.warningAlert
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class EmployeeListView(val employee: Employee) : ListView<DateTime>(employee.attendances) {

    init {
        prefWidth = 128.0
        setCellFactory { _ ->
            object : ListCell<DateTime>() {
                override fun updateItem(item: DateTime?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = when {
                        item == null || empty -> null
                        else -> DateTimeFormat.forPattern(PATTERN_DATETIME).print(item)
                    }
                }
            }
        }
        contextMenu = ContextMenu(
                MenuItem(getString(R.string.delete)).apply {
                    setOnAction {
                        when {
                            selectionModel.isNotSelected -> warningAlert(employee.name, getString(R.string.error_no_selection)).show()
                            else -> {
                                items.removeAt(selectionModel.selectedIndex)
                                Collections.sort(items)
                            }
                        }
                    }
                })
        if (BuildConfig.DEBUG) {
            // double click to remove for quick testing
            setOnMouseClicked { event ->
                if (event.button == MouseButton.PRIMARY && event.clickCount == 2 && (event.target is LabeledText || (event.target as GridPane).children.size > 0)) {
                    if (selectionModel.isSelected) {
                        items.removeAt(selectionModel.selectedIndex)
                        Collections.sort(items)
                    }
                }
            }
        }
    }
}