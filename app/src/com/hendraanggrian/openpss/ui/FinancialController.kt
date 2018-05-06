package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.Popup
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.currencyConverter
import javafx.geometry.Orientation.VERTICAL
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.SelectionModel
import javafx.scene.control.TableView
import javafx.scene.image.ImageView
import ktfx.coroutines.onAction
import ktfx.coroutines.onMouseClicked
import ktfx.layouts.button
import ktfx.layouts.contextMenu
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.menuItem
import ktfx.layouts.separator
import ktfx.layouts.tooltip
import ktfx.scene.input.isDoubleClick
import ktfx.scene.layout.gap
import java.net.URL
import java.util.ResourceBundle

abstract class FinancialController<T> : SegmentedController(), Refreshable, Selectable<T> {

    abstract val table: TableView<T>

    abstract val List<T>.totalCash: Double

    abstract val List<T>.totalTransfer: Double

    abstract fun view(item: T)

    private lateinit var refreshButton: Button
    private lateinit var viewTotalButton: Button
    override val leftButtons: List<Node> get() = listOf(refreshButton, separator(VERTICAL), viewTotalButton)

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        refreshButton = button(graphic = ImageView(R.image.btn_refresh)) {
            tooltip(getString(R.string.refresh))
            onAction { refresh() }
        }
        viewTotalButton = button(graphic = ImageView(R.image.btn_money)) {
            tooltip(getString(R.string.view_total))
            onAction { ViewTotalPopup(this@FinancialController).show(this@button) }
        }
        table.onMouseClicked { if (it.isDoubleClick() && selected != null) view(selected!!) }
        table.contextMenu {
            menuItem(getString(R.string.view)) {
                disableProperty().bind(!selectedBinding)
                onAction { view(selected!!) }
            }
        }
    }

    override val selectionModel: SelectionModel<T> get() = table.selectionModel

    inner class ViewTotalPopup(
        resourced: Resourced
    ) : Popup<Nothing>(resourced, R.string.view_total) {

        override val content: Node
            get() = gridPane {
                gap = 8.0
                val cash = table.items.totalCash
                val transfer = table.items.totalCash
                label(getString(R.string.cash)) col 0 row 0
                label(currencyConverter.toString(cash)) col 1 row 0
                label(getString(R.string.cash)) col 0 row 1
                label(currencyConverter.toString(transfer)) col 1 row 1
                label(getString(R.string.cash)) col 0 row 2
                label(currencyConverter.toString(cash + transfer)) col 1 row 2
            }
    }
}