package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.PATTERN_DATE
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.schema.Customer
import com.hendraanggrian.openpss.schema.Invoice
import com.hendraanggrian.openpss.schema.typedTechnique
import com.hendraanggrian.openpss.ui.ResultableDialog
import com.hendraanggrian.openpss.ui.ResultablePopOver
import com.hendraanggrian.openpss.ui.invoice.job.AddDigitalJobPopOver
import com.hendraanggrian.openpss.ui.invoice.job.AddOffsetJobPopOver
import com.hendraanggrian.openpss.ui.invoice.job.AddOtherJobPopOver
import com.hendraanggrian.openpss.ui.invoice.job.AddPlateJobPopOver
import com.hendraanggrian.openpss.util.currencyCell
import com.hendraanggrian.openpss.util.numberCell
import com.hendraanggrian.openpss.util.stringCell
import javafx.beans.binding.When
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ktfx.bindings.doubleBindingOf
import ktfx.bindings.greater
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.bindings.otherwise
import ktfx.bindings.stringBindingOf
import ktfx.bindings.then
import ktfx.collections.isEmptyBinding
import ktfx.collections.mutableObservableListOf
import ktfx.controls.TableColumnsBuilder
import ktfx.controls.columns
import ktfx.controls.isSelected
import ktfx.controls.notSelectedBinding
import ktfx.coroutines.onAction
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.inputs.isDelete
import ktfx.invoke
import ktfx.jfoenix.layouts.jfxTabPane
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.NodeManager
import ktfx.layouts.contextMenu
import ktfx.layouts.gap
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.tab
import ktfx.layouts.tableView
import ktfx.layouts.textArea
import ktfx.runLater
import org.joda.time.DateTime

class AddInvoiceDialog(
    component: FxComponent
) : ResultableDialog<Invoice>(component, R2.string.add_invoice) {

    private val customerField: TextField
    private val offsetTable: TableView<Invoice.OffsetJob>
    private val digitalTable: TableView<Invoice.DigitalJob>
    private val plateTable: TableView<Invoice.PlateJob>
    private val otherTable: TableView<Invoice.OtherJob>
    private val noteArea: TextArea

    private val dateTime: DateTime = runBlocking(Dispatchers.IO) { OpenPSSApi.getDateTime() }
    private val customerProperty: ObjectProperty<Customer> = SimpleObjectProperty(null)
    private val totalProperty: DoubleProperty = SimpleDoubleProperty()

    override val focusedNode: Node? get() = customerField

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            label(getString(R2.string.employee)) {
                gridAt(0, 0)
            }
            label(login.name) {
                gridAt(0, 1)
                styleClass += R.style.bold
            }
            label(getString(R2.string.date)) {
                gridAt(0, 2)
                hgrow()
                halignRight()
            }
            label(dateTime.toString(PATTERN_DATE)) {
                gridAt(0, 3)
                styleClass += R.style.bold
            }
            label(getString(R2.string.customer)) {
                gridAt(1, 0)
            }
            customerField = jfxTextField {
                gridAt(1, 1)
                isEditable = false
                textProperty().bind(stringBindingOf(customerProperty) {
                    customerProperty.value?.toString() ?: getString(R2.string.search_customer)
                })
                onMouseClicked {
                    SearchCustomerPopOver(this@AddInvoiceDialog).show(this@jfxTextField) {
                        customerProperty.set(it)
                    }
                }
            }
            label(getString(R2.string.jobs)) {
                gridAt(2, 0)
            }
            jfxTabPane {
                gridAt(2, 1, colSpans = 3)
                styleClass += R.style.jfx_tab_pane_small
                tab {
                    digitalTable =
                        invoiceTableView({ AddDigitalJobPopOver(this@AddInvoiceDialog) }) {
                            bindTitle(this, R2.string.digital)
                            columns {
                                column<Invoice.DigitalJob, String>(R2.string.qty, 72) {
                                    numberCell(this@AddInvoiceDialog) { qty }
                                }
                                column<Invoice.DigitalJob, String>(R2.string.type, 72) {
                                    stringCell { type }
                                }
                                column<Invoice.DigitalJob, String>(R2.string.description, 264) {
                                    stringCell { desc }
                                }
                                column<Invoice.DigitalJob, String>(R2.string.total, 156) {
                                    currencyCell(this@AddInvoiceDialog) { total }
                                }
                            }
                        }
                }
                tab {
                    offsetTable = invoiceTableView({ AddOffsetJobPopOver(this@AddInvoiceDialog) }) {
                        bindTitle(this, R2.string.offset)
                        columns {
                            column<Invoice.OffsetJob, String>(R2.string.qty, 72) {
                                numberCell(this@AddInvoiceDialog) { qty }
                            }
                            column<Invoice.OffsetJob, String>(R2.string.type, 72) {
                                stringCell { type }
                            }
                            column<Invoice.OffsetJob, String>(R2.string.technique, 72) {
                                stringCell { typedTechnique.toString(this@AddInvoiceDialog) }
                            }
                            column<Invoice.OffsetJob, String>(R2.string.description, 192) {
                                stringCell { desc }
                            }
                            column<Invoice.OffsetJob, String>(R2.string.total, 156) {
                                currencyCell(this@AddInvoiceDialog) { total }
                            }
                        }
                    }
                }
                tab {
                    plateTable = invoiceTableView({ AddPlateJobPopOver(this@AddInvoiceDialog) }) {
                        bindTitle(this, R2.string.plate)
                        columns {
                            column<Invoice.PlateJob, String>(R2.string.qty, 72) {
                                numberCell(this@AddInvoiceDialog) { qty }
                            }
                            column<Invoice.PlateJob, String>(R2.string.type, 72) {
                                stringCell { type }
                            }
                            column<Invoice.PlateJob, String>(R2.string.description, 264) {
                                stringCell { desc }
                            }
                            column<Invoice.PlateJob, String>(R2.string.total, 156) {
                                currencyCell(this@AddInvoiceDialog) { total }
                            }
                        }
                    }
                }
                tab {
                    otherTable = invoiceTableView({ AddOtherJobPopOver(this@AddInvoiceDialog) }) {
                        bindTitle(this, R2.string.others)
                        columns {
                            column<Invoice.OtherJob, String>(R2.string.qty, 72) {
                                numberCell(this@AddInvoiceDialog) { qty }
                            }
                            column<Invoice.OtherJob, String>(R2.string.description, 336) {
                                stringCell { desc }
                            }
                            column<Invoice.OtherJob, String>(R2.string.total, 156) {
                                currencyCell(this@AddInvoiceDialog) { total }
                            }
                        }
                    }
                }
            }
            totalProperty.bind(doubleBindingOf(
                offsetTable.items,
                digitalTable.items,
                plateTable.items,
                otherTable.items
            ) {
                offsetTable.items.sumByDouble { it.total } +
                    digitalTable.items.sumByDouble { it.total } +
                    plateTable.items.sumByDouble { it.total } +
                    otherTable.items.sumByDouble { it.total }
            })
            label(getString(R2.string.note)) {
                gridAt(3, 0)
            }
            noteArea = textArea {
                gridAt(3, 1, colSpans = 3)
                promptText = getString(R2.string.note)
                prefHeight = 64.0
            }
            label(getString(R2.string.total)) {
                gridAt(4, 0)
            }
            label {
                gridAt(4, 1)
                styleClass += R.style.bold
                textProperty().bind(stringBindingOf(totalProperty) {
                    currencyConverter(totalProperty.value)
                })
                textFillProperty().bind(
                    When(totalProperty greater 0)
                        then getColor(R.value.color_green)
                        otherwise getColor(R.value.color_red)
                )
            }
        }
        defaultButton.disableProperty().bind(customerProperty.isNull or totalProperty.lessEq(0))
    }

    override val nullableResult: Invoice?
        get() = Invoice.new(
            runBlocking(Dispatchers.IO) { OpenPSSApi.nextInvoice() },
            login.id,
            customerProperty.value.id,
            dateTime,
            digitalTable.items,
            offsetTable.items,
            plateTable.items,
            otherTable.items,
            noteArea.text
        )

    private fun <S> NodeManager.invoiceTableView(
        newAddJobPopOver: () -> ResultablePopOver<S>,
        init: TableView<S>.() -> Unit
    ): TableView<S> = tableView {
        prefHeight = 128.0
        init()
        items = mutableObservableListOf<S>()
        contextMenu {
            getString(R2.string.add)(ImageView(R.image.menu_add)) {
                onAction { newAddJobPopOver().show(this@tableView) { this@tableView.items.add(it) } }
            }
            separatorMenuItem()
            getString(R2.string.delete)(ImageView(R.image.menu_delete)) {
                runLater { disableProperty().bind(this@tableView.selectionModel.notSelectedBinding) }
                onAction { this@tableView.items.remove(this@tableView.selectionModel.selectedItem) }
            }
            getString(R2.string.clear)(ImageView(R.image.menu_clear)) {
                runLater { disableProperty().bind(this@tableView.items.isEmptyBinding) }
                onAction { this@tableView.items.clear() }
            }
        }
        onKeyPressed {
            if (it.code.isDelete() && selectionModel.isSelected()) {
                items.remove(selectionModel.selectedItem)
            }
        }
    }

    private fun <S, T> TableColumnsBuilder<S>.column(
        textId: String,
        width: Int,
        init: TableColumn<S, T>.() -> Unit
    ): TableColumn<S, T> = column(getString(textId)) {
        width.toDouble().let {
            minWidth = it
            prefWidth = it
            maxWidth = it
        }
        init()
    }

    private fun Tab.bindTitle(tableView: TableView<*>, s: String) =
        textProperty().bind(stringBindingOf(tableView.items) {
            getString(s).let {
                when {
                    tableView.items.isEmpty() -> it
                    else -> "$it (${tableView.items.size})"
                }
            }
        })
}
