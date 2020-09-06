package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.content.PATTERN_DATE
import com.hendraanggrian.openpss.db.dbDateTime
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.popup.dialog.ResultableDialog
import com.hendraanggrian.openpss.popup.popover.ResultablePopover
import com.hendraanggrian.openpss.ui.invoice.job.AddDigitalJobPopover
import com.hendraanggrian.openpss.ui.invoice.job.AddOffsetJobPopover
import com.hendraanggrian.openpss.ui.invoice.job.AddOtherJobPopover
import com.hendraanggrian.openpss.ui.invoice.job.AddPlateJobPopover
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
import ktfx.bindings.asString
import ktfx.bindings.doubleBindingOf
import ktfx.bindings.greater
import ktfx.bindings.isEmpty
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.bindings.otherwise
import ktfx.bindings.stringBindingOf
import ktfx.bindings.then
import ktfx.controls.H_RIGHT
import ktfx.controls.TableColumnScope
import ktfx.controls.columns
import ktfx.controls.isSelected
import ktfx.coroutines.onAction
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.inputs.isDelete
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.jfoenix.layouts.styledJFXTabPane
import ktfx.layouts.NodeManager
import ktfx.layouts.contextMenu
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.styledLabel
import ktfx.layouts.tab
import ktfx.layouts.tableView
import ktfx.layouts.textArea
import ktfx.runLater
import ktfx.text.invoke
import org.joda.time.DateTime

class AddInvoiceDialog(
    context: Context
) : ResultableDialog<Invoice>(context, R.string.add_invoice) {

    private var customerField: TextField
    private lateinit var offsetTable: TableView<Invoice.OffsetJob>
    private lateinit var digitalTable: TableView<Invoice.DigitalJob>
    private lateinit var plateTable: TableView<Invoice.PlateJob>
    private lateinit var otherTable: TableView<Invoice.OtherJob>
    private var noteArea: TextArea

    private val dateTime: DateTime = dbDateTime
    private val customerProperty: ObjectProperty<Customer> = SimpleObjectProperty(null)
    private val totalProperty: DoubleProperty = SimpleDoubleProperty()

    override val focusedNode: Node? get() = customerField

    init {
        gridPane {
            hgap = getDouble(R.dimen.padding_medium)
            vgap = getDouble(R.dimen.padding_medium)
            label(getString(R.string.employee)).grid(0, 0)
            styledLabel(login.name, null, R.style.bold).grid(0, 1)
            label(getString(R.string.date)).grid(0, 2).hgrow().halign(H_RIGHT)
            styledLabel(dateTime.toString(PATTERN_DATE), null, R.style.bold).grid(0, 3)
            label(getString(R.string.customer)).grid(1, 0)
            customerField = jfxTextField {
                isEditable = false
                textProperty().bind(
                    customerProperty.asString { it?.toString() ?: getString(R.string.search_customer) }
                )
                onMouseClicked {
                    SearchCustomerPopover(this@AddInvoiceDialog).show(this@jfxTextField) { customerProperty.set(it) }
                }
            }.grid(1, 1)
            label(getString(R.string.jobs)).grid(2, 0)
            styledJFXTabPane(styleClass = arrayOf(R.style.jfx_tab_pane_small)) {
                tab {
                    digitalTable = invoiceTableView({ AddDigitalJobPopover(this@AddInvoiceDialog) }) {
                        bindTitle(this, R.string.digital)
                        columns {
                            append<Invoice.DigitalJob, String>(R.string.qty, 72) {
                                numberCell(this@AddInvoiceDialog) { qty }
                            }
                            append<Invoice.DigitalJob, String>(R.string.type, 72) {
                                stringCell { type }
                            }
                            append<Invoice.DigitalJob, String>(R.string.description, 264) {
                                stringCell { desc }
                            }
                            append<Invoice.DigitalJob, String>(R.string.total, 156) {
                                currencyCell(this@AddInvoiceDialog) { total }
                            }
                        }
                    }
                }
                tab {
                    offsetTable = invoiceTableView({ AddOffsetJobPopover(this@AddInvoiceDialog) }) {
                        bindTitle(this, R.string.offset)
                        columns {
                            append<Invoice.OffsetJob, String>(R.string.qty, 72) {
                                numberCell(this@AddInvoiceDialog) { qty }
                            }
                            append<Invoice.OffsetJob, String>(R.string.type, 72) {
                                stringCell { type }
                            }
                            append<Invoice.OffsetJob, String>(R.string.technique, 72) {
                                stringCell { typedTechnique.toString(this@AddInvoiceDialog) }
                            }
                            append<Invoice.OffsetJob, String>(R.string.description, 192) {
                                stringCell { desc }
                            }
                            append<Invoice.OffsetJob, String>(R.string.total, 156) {
                                currencyCell(this@AddInvoiceDialog) { total }
                            }
                        }
                    }
                }
                tab {
                    plateTable = invoiceTableView({ AddPlateJobPopover(this@AddInvoiceDialog) }) {
                        bindTitle(this, R.string.plate)
                        columns {
                            append<Invoice.PlateJob, String>(R.string.qty, 72) {
                                numberCell(this@AddInvoiceDialog) { qty }
                            }
                            append<Invoice.PlateJob, String>(R.string.type, 72) {
                                stringCell { type }
                            }
                            append<Invoice.PlateJob, String>(R.string.description, 264) {
                                stringCell { desc }
                            }
                            append<Invoice.PlateJob, String>(R.string.total, 156) {
                                currencyCell(this@AddInvoiceDialog) { total }
                            }
                        }
                    }
                }
                tab {
                    otherTable = invoiceTableView({ AddOtherJobPopover(this@AddInvoiceDialog) }) {
                        bindTitle(this, R.string.others)
                        columns {
                            append<Invoice.OtherJob, String>(R.string.qty, 72) {
                                numberCell(this@AddInvoiceDialog) { qty }
                            }
                            append<Invoice.OtherJob, String>(R.string.description, 336) {
                                stringCell { desc }
                            }
                            append<Invoice.OtherJob, String>(R.string.total, 156) {
                                currencyCell(this@AddInvoiceDialog) { total }
                            }
                        }
                    }
                }
            }.grid(2, 1 to 3)
            totalProperty.bind(
                doubleBindingOf(
                    offsetTable.items,
                    digitalTable.items,
                    plateTable.items,
                    otherTable.items
                ) {
                    offsetTable.items.sumByDouble { it.total } +
                        digitalTable.items.sumByDouble { it.total } +
                        plateTable.items.sumByDouble { it.total } +
                        otherTable.items.sumByDouble { it.total }
                }
            )
            label(getString(R.string.note)).grid(3, 0)
            noteArea = textArea {
                promptText = getString(R.string.note)
                prefHeight = 64.0
            }.grid(3, 1 to 3)
            label(getString(R.string.total)).grid(4, 0)
            styledLabel(styleClass = arrayOf(R.style.bold)) {
                textProperty().bind(totalProperty.asString { currencyConverter(it) })
                textFillProperty().bind(
                    When(totalProperty greater 0)
                        then getColor(R.color.green)
                        otherwise getColor(R.color.red)
                )
            }.grid(4, 1)
        }
        defaultButton.disableProperty().bind(customerProperty.isNull or totalProperty.lessEq(0))
    }

    override val nullableResult: Invoice?
        get() = Invoice.new(
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
        newAddJobPopover: () -> ResultablePopover<S>,
        init: TableView<S>.() -> Unit
    ): TableView<S> = tableView {
        prefHeight = 128.0
        init()
        contextMenu {
            getString(R.string.add)(ImageView(R.image.menu_add)) {
                onAction { newAddJobPopover().show(this@tableView) { this@tableView.items.add(it) } }
            }
            separatorMenuItem()
            getString(R.string.delete)(ImageView(R.image.menu_delete)) {
                runLater { disableProperty().bind(this@tableView.selectionModel.selectedItemProperty().isNull) }
                onAction { this@tableView.items.remove(this@tableView.selectionModel.selectedItem) }
            }
            getString(R.string.clear)(ImageView(R.image.menu_clear)) {
                runLater { disableProperty().bind(this@tableView.items.isEmpty) }
                onAction { this@tableView.items.clear() }
            }
        }
        onKeyPressed {
            if (it.isDelete() && selectionModel.isSelected()) {
                items.remove(selectionModel.selectedItem)
            }
        }
    }

    private fun <S, T> TableColumnScope<S>.append(
        textId: String,
        width: Int,
        init: TableColumn<S, T>.() -> Unit
    ): TableColumn<S, T> = append(getString(textId)) {
        width.toDouble().let {
            minWidth = it
            prefWidth = it
            maxWidth = it
        }
        init()
    }

    private fun Tab.bindTitle(tableView: TableView<*>, s: String) =
        textProperty().bind(
            stringBindingOf(tableView.items) {
                getString(s).let {
                    when {
                        tableView.items.isEmpty() -> it
                        else -> "$it (${tableView.items.size})"
                    }
                }
            }
        )
}
