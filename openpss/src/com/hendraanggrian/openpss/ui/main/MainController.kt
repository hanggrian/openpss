package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.PATTERN_DATETIME
import com.hendraanggrian.openpss.control.PaginatedPane
import com.hendraanggrian.openpss.control.UnselectableListView
import com.hendraanggrian.openpss.control.popover.ViewInvoicePopover
import com.hendraanggrian.openpss.db.dbDateTime
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Log
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.ActionController
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.Selectable2
import com.hendraanggrian.openpss.ui.customer.CustomerController
import com.hendraanggrian.openpss.ui.finance.FinanceController
import com.hendraanggrian.openpss.ui.invoice.InvoiceController
import com.hendraanggrian.openpss.ui.main.edit.EditEmployeeDialog
import com.hendraanggrian.openpss.ui.main.edit.price.EditDigitalPrintPriceDialog
import com.hendraanggrian.openpss.ui.main.edit.price.EditOffsetPrintPriceDialog
import com.hendraanggrian.openpss.ui.main.edit.price.EditPlatePriceDialog
import com.hendraanggrian.openpss.ui.main.help.AboutDialog
import com.hendraanggrian.openpss.ui.main.help.GitHubApi
import com.hendraanggrian.openpss.ui.schedule.ScheduleController
import com.hendraanggrian.openpss.ui.wage.EditRecessDialog
import com.hendraanggrian.openpss.ui.wage.WageController
import com.jfoenix.controls.JFXDrawer
import com.jfoenix.controls.JFXHamburger
import com.jfoenix.controls.JFXToolbar
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.SelectionModel
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.util.Callback
import ktfx.application.later
import ktfx.beans.binding.`when`
import ktfx.beans.binding.buildStringBinding
import ktfx.beans.binding.minus
import ktfx.beans.binding.otherwise
import ktfx.beans.binding.then
import ktfx.beans.value.eq
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.jfoenix.jfxSnackbar
import ktfx.layouts.text
import ktfx.listeners.cellFactory
import ktfx.scene.text.fontSize
import org.apache.commons.lang3.SystemUtils
import java.net.URL
import java.util.ResourceBundle
import kotlin.math.ceil

class MainController : Controller(), Selectable<Tab>, Selectable2<Label> {

    @FXML override lateinit var root: StackPane
    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var addCustomerItem: MenuItem
    @FXML lateinit var addInvoiceItem: MenuItem
    @FXML lateinit var quitItem: MenuItem
    @FXML lateinit var platePriceItem: MenuItem
    @FXML lateinit var offsetPrintPriceItem: MenuItem
    @FXML lateinit var digitalPrintPriceItem: MenuItem
    @FXML lateinit var employeeItem: MenuItem
    @FXML lateinit var recessItem: MenuItem
    @FXML lateinit var preferencesItem: MenuItem
    @FXML lateinit var drawer: JFXDrawer
    @FXML lateinit var drawerList: ListView<Label>
    @FXML lateinit var eventPagination: PaginatedPane
    @FXML lateinit var toolbar: JFXToolbar
    @FXML lateinit var hamburger: JFXHamburger
    @FXML lateinit var employeeLabel: Label
    @FXML lateinit var titleLabel: Label
    @FXML lateinit var tabPane: TabPane
    @FXML lateinit var customerGraphic: ImageView
    @FXML lateinit var invoiceGraphic: ImageView
    @FXML lateinit var scheduleGraphic: ImageView
    @FXML lateinit var financeGraphic: ImageView
    @FXML lateinit var wageGraphic: ImageView
    @FXML lateinit var customerController: CustomerController
    @FXML lateinit var invoiceController: InvoiceController
    @FXML lateinit var scheduleController: ScheduleController
    @FXML lateinit var financeController: FinanceController
    @FXML lateinit var wageController: WageController

    override val selectionModel: SelectionModel<Tab> get() = tabPane.selectionModel
    override val selectionModel2: SelectionModel<Label> get() = drawerList.selectionModel

    private val controllers: List<ActionController>
        get() = mutableListOf(
            customerController,
            invoiceController,
            scheduleController,
            financeController,
            wageController
        )

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        menuBar.isUseSystemMenuBar = SystemUtils.IS_OS_MAC

        selectFirst2()
        selectedProperty2.listener { _, _, _ ->
            select(selectedIndex2)
            drawer.toggle()
        }
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED) { drawer.toggle() }
        titleLabel.textProperty().bind(buildStringBinding(selectedProperty2) { selected2?.text })

        customerGraphic.bind(0, R.image.tab_customer2, R.image.tab_customer)
        invoiceGraphic.bind(1, R.image.tab_invoice2, R.image.tab_invoice)
        scheduleGraphic.bind(2, R.image.tab_schedule2, R.image.tab_schedule)
        financeGraphic.bind(3, R.image.tab_finance2, R.image.tab_finance)
        wageGraphic.bind(4, R.image.tab_wage2, R.image.tab_wage)

        customerController.replaceButtons()
        selectedIndexProperty.listener { _, _, value ->
            controllers[value.toInt()].let {
                it.replaceButtons()
                (it as? Refreshable)?.refresh()
            }
        }

        later {
            drawer.toggle()
            employeeLabel.text = login.name
            controllers.forEach {
                it.login = login
                it.root = root
            }
            financeController.addExtra(FinanceController.EXTRA_MAIN_CONTROLLER, this)
        }
    }

    @FXML fun add(event: ActionEvent) = when (event.source) {
        addCustomerItem -> select(customerController) { later { add() } }
        else -> select(invoiceController) { addInvoice() }
    }

    @FXML fun quit() = App.exit()

    @FXML fun editPrice(event: ActionEvent) = when (event.source) {
        platePriceItem -> EditPlatePriceDialog(this)
        offsetPrintPriceItem -> EditOffsetPrintPriceDialog(this)
        else -> EditDigitalPrintPriceDialog(this)
    }.show()

    @FXML fun editEmployee() = EditEmployeeDialog(this).show()

    @FXML fun editRecess() = EditRecessDialog(this).show()

    @FXML fun preferences() = PreferencesDialog(this).show()

    @FXML fun testViewInvoice() {
        val customer = transaction { Customers().firstOrNull() }
        when (customer) {
            null -> root.jfxSnackbar(getString(R.string.no_customer_to_test), App.DURATION_SHORT)
            else -> ViewInvoicePopover(
                this,
                Invoice(
                    no = 1234,
                    employeeId = login.id,
                    customerId = customer.id,
                    dateTime = dbDateTime,
                    offsetJobs = listOf(
                        Invoice.OffsetJob.new(
                            5, "Title", 92000.0, "Type",
                            Invoice.OffsetJob.Technique.TWO_SIDE_EQUAL
                        )
                    ),
                    digitalJobs = listOf(Invoice.DigitalJob.new(5, "Title", 92000.0, "Type", false)),
                    plateJobs = listOf(Invoice.PlateJob.new(5, "Title", 92000.0, "Type")),
                    otherJobs = listOf(Invoice.OtherJob.new(5, "Title", 92000.0)),
                    note = "This is a test",
                    printed = false,
                    paid = false,
                    done = false
                ), true
            ).show(menuBar)
        }
    }

    @FXML fun checkUpdate() = GitHubApi.checkUpdates(this)

    @FXML fun about() = AboutDialog(this).show()

    @FXML fun toggleHamburger() {
        if (drawer.isOpening) {
            eventPagination.contentFactory = Callback { (page, count) ->
                UnselectableListView<Log>().apply {
                    styleClass.addAll("pane-borderless", "list-view-no-horizontal-scrollbar")
                    cellFactory {
                        onUpdate { event, empty ->
                            if (event != null && !empty) graphic = ktfx.layouts.textFlow {
                                text(event.message) {
                                    isWrapText = true
                                    fontSize = 12.0
                                    this@textFlow.prefWidthProperty().bind(this@apply.widthProperty() - 12)
                                    wrappingWidthProperty().bind(this@apply.widthProperty())
                                }
                                newLine()
                                text(
                                    event.dateTime.toString(PATTERN_DATETIME) + " " +
                                        transaction { Employees[event.employeeId].single().name }
                                ) {
                                    styleClass += "bold"
                                }
                            }
                        }
                    }
                    later {
                        transaction {
                            val logs = Logs()
                            eventPagination.pageCount = ceil(logs.count() / count.toDouble()).toInt()
                            items = logs
                                .skip(count * page)
                                .take(count)
                                .toObservableList()
                        }
                    }
                }
            }
            if (!drawerList.isFocused) {
                drawerList.requestFocus()
            }
        }
    }

    private fun ActionController.replaceButtons() = toolbar.setRightItems(*actions.toTypedArray())

    private fun <T : ActionController> select(controller: T, run: T.() -> Unit) {
        select(controllers.indexOf(controller))
        controller.run(run)
    }

    private fun ImageView.bind(index: Int, selectedImageId: String, unselectedImageId: String) = imageProperty().bind(
        `when`(selectedIndexProperty2 eq index)
            then Image(selectedImageId)
            otherwise Image(unselectedImageId)
    )
}