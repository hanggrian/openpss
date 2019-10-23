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
import javafx.geometry.HPos.RIGHT
import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority.ALWAYS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ktfx.bindings.buildDoubleBinding
import ktfx.bindings.buildStringBinding
import ktfx.bindings.greater
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.bindings.otherwise
import ktfx.bindings.then
import ktfx.collections.isEmptyBinding
import ktfx.collections.mutableObservableListOf
import ktfx.controls.gap
import ktfx.controls.isSelected
import ktfx.coroutines.onAction
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.inputs.isDelete
import ktfx.invoke
import ktfx.jfoenix.jfxTabPane
import ktfx.jfoenix.jfxTextField
import ktfx.layouts.NodeManager
import ktfx.layouts.TableColumnsBuilder
import ktfx.layouts.columns
import ktfx.layouts.contextMenu
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

    private lateinit var customerField: TextField
    private lateinit var offsetTable: TableView<Invoice.OffsetJob>
    private lateinit var digitalTable: TableView<Invoice.DigitalJob>
    private lateinit var plateTable: TableView<Invoice.PlateJob>
    private lateinit var otherTable: TableView<Invoice.OtherJob>
    private lateinit var noteArea: TextArea

    private val dateTime: DateTime = runBlocking(Dispatchers.IO) { OpenPSSApi.getDateTime() }
    private val customerProperty: ObjectProperty<Customer> = SimpleObjectProperty(null)
    private val totalProperty: DoubleProperty = SimpleDoubleProperty()

    override val focusedNode: Node? get() = customerField

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            label(getString(R2.string.employee)) col 0 row 0
            label(login.name) {
                styleClass += R.style.bold
            } col 1 row 0
            label(getString(R2.string.date)) col 2 row 0 hpriority ALWAYS halign RIGHT
            label(dateTime.toString(PATTERN_DATE)) {
                styleClass += R.style.bold
            } col 3 row 0
            label(getString(R2.string.customer)) col 0 row 1
            customerField = jfxTextField {
                isEditable = false
                textProperty().bind(buildStringBinding(customerProperty) {
                    customerProperty.value?.toString() ?: getString(R2.string.search_customer)
                })
                onMouseClicked {
                    SearchCustomerPopOver(this@AddInvoiceDialog).show(this@jfxTextField) {
                        customerProperty.set(it)
                    }
                }
            } col 1 row 1
            label(getString(R2.string.jobs)) col 0 row 2
            jfxTabPane {
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
            } col 1 row 2 colSpans 3
            totalProperty.bind(buildDoubleBinding(
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
            label(getString(R2.string.note)) col 0 row 3
            noteArea = textArea {
                promptText = getString(R2.string.note)
                prefHeight = 64.0
            } col 1 row 3 colSpans 3
            label(getString(R2.string.total)) col 0 row 4
            label {
                styleClass += R.style.bold
                textProperty().bind(buildStringBinding(totalProperty) {
                    currencyConverter(totalProperty.value)
                })
                textFillProperty().bind(
                    When(totalProperty greater 0)
                        then getColor(R.value.color_green)
                        otherwise getColor(R.value.color_red)
                )
            } col 1 row 4
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
                runLater { disableProperty().bind(this@tableView.selectionModel.selectedItemProperty().isNull) }
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
        textProperty().bind(buildStringBinding(tableView.items) {
            getString(s).let {
                when {
                    tableView.items.isEmpty() -> it
                    else -> "$it (${tableView.items.size})"
                }
            }
        })
}
