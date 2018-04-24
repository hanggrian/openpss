package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.util.currencyConverter
import com.hendraanggrian.openpss.util.getFont
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.SelectionModel
import javafx.scene.control.TableView
import ktfx.beans.binding.stringBindingOf
import java.net.URL
import java.util.ResourceBundle

abstract class FinancialController<T> : Controller(), Refreshable, Selectable<T> {

    abstract val table: TableView<T>

    abstract fun List<T>.getTotalCash(): Double

    abstract fun List<T>.getTransferCash(): Double

    @FXML lateinit var totalCashLabel1: Label
    @FXML lateinit var totalCashLabel2: Label
    @FXML lateinit var totalTransferLabel1: Label
    @FXML lateinit var totalTransferLabel2: Label
    @FXML lateinit var totalAllLabel1: Label
    @FXML lateinit var totalAllLabel2: Label

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        totalAllLabel1.font = getFont(R.font.sf_pro_text_bold)
        totalCashLabel1.font = getFont(R.font.sf_pro_text_bold)
        totalTransferLabel1.font = getFont(R.font.sf_pro_text_bold)
        totalCashLabel2.textProperty().bind(stringBindingOf(table.itemsProperty()) {
            currencyConverter.toString(table.items.getTotalCash())
        })
        totalTransferLabel2.textProperty().bind(stringBindingOf(table.itemsProperty()) {
            currencyConverter.toString(table.items.getTransferCash())
        })
        totalAllLabel2.textProperty().bind(
            stringBindingOf(totalCashLabel2.textProperty(), totalTransferLabel2.textProperty()) {
                currencyConverter.toString(currencyConverter.fromString(totalCashLabel2.text).toDouble() +
                    currencyConverter.fromString(totalTransferLabel2.text).toDouble())
            })
    }

    override val selectionModel: SelectionModel<T> get() = table.selectionModel
}