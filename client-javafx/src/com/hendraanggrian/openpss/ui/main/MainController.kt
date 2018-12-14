package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.BuildConfig
import com.hendraanggrian.openpss.PATTERN_DATETIME
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.MarginedImageView
import com.hendraanggrian.openpss.control.PaginatedPane
import com.hendraanggrian.openpss.control.Toolbar
import com.hendraanggrian.openpss.control.UnselectableListView
import com.hendraanggrian.openpss.db.Database
import com.hendraanggrian.openpss.db.schema.Technique
import com.hendraanggrian.openpss.db.schema.new
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Log
import com.hendraanggrian.openpss.popup.popover.ViewInvoicePopover
import com.hendraanggrian.openpss.ui.ActionController
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.customer.CustomerController
import com.hendraanggrian.openpss.ui.employee.EditEmployeeDialog
import com.hendraanggrian.openpss.ui.finance.FinanceController
import com.hendraanggrian.openpss.ui.invoice.InvoiceController
import com.hendraanggrian.openpss.ui.main.help.AboutDialog
import com.hendraanggrian.openpss.ui.main.help.GitHubHelper
import com.hendraanggrian.openpss.ui.price.EditDigitalPriceDialog
import com.hendraanggrian.openpss.ui.price.EditOffsetPriceDialog
import com.hendraanggrian.openpss.ui.price.EditPlatePriceDialog
import com.hendraanggrian.openpss.ui.schedule.ScheduleController
import com.hendraanggrian.openpss.ui.wage.EditRecessDialog
import com.hendraanggrian.openpss.ui.wage.WageController
import com.jfoenix.controls.JFXDrawer
import com.jfoenix.controls.JFXHamburger
import javafx.beans.binding.When
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.TabPane
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.util.Callback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.application.later
import ktfx.beans.binding.buildStringBinding
import ktfx.beans.binding.minus
import ktfx.beans.binding.otherwise
import ktfx.beans.binding.then
import ktfx.beans.property.hasValue
import ktfx.beans.value.eq
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.jfoenix.jfxSnackbar
import ktfx.layouts.text
import ktfx.layouts.textFlow
import ktfx.listeners.cellFactory
import ktfx.scene.stage
import ktfx.scene.text.fontSize
import org.apache.commons.lang3.SystemUtils
import java.net.URL
import java.util.ResourceBundle

class MainController : Controller(), Refreshable {

    @FXML override lateinit var rootLayout: StackPane
    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var addCustomerItem: MenuItem
    @FXML lateinit var addInvoiceItem: MenuItem
    @FXML lateinit var quitItem: MenuItem
    @FXML lateinit var platePriceItem: MenuItem
    @FXML lateinit var offsetPrintPriceItem: MenuItem
    @FXML lateinit var digitalPrintPriceItem: MenuItem
    @FXML lateinit var employeeItem: MenuItem
    @FXML lateinit var recessItem: MenuItem
    @FXML lateinit var settingsItem: MenuItem
    @FXML lateinit var drawer: JFXDrawer
    @FXML lateinit var drawerList: ListView<Label>
    @FXML lateinit var eventPagination: PaginatedPane
    @FXML lateinit var toolbar: Toolbar
    @FXML lateinit var hamburger: JFXHamburger
    @FXML lateinit var employeeLabel: Label
    @FXML lateinit var titleLabel: Label
    @FXML lateinit var tabPane: TabPane
    @FXML lateinit var customerGraphic: MarginedImageView
    @FXML lateinit var invoiceGraphic: MarginedImageView
    @FXML lateinit var scheduleGraphic: MarginedImageView
    @FXML lateinit var financeGraphic: MarginedImageView
    @FXML lateinit var wageGraphic: MarginedImageView
    @FXML lateinit var customerController: CustomerController
    @FXML lateinit var invoiceController: InvoiceController
    @FXML lateinit var scheduleController: ScheduleController
    @FXML lateinit var financeController: FinanceController
    @FXML lateinit var wageController: WageController

    private val controllers: List<ActionController>
        get() = listOf(
            customerController,
            invoiceController,
            scheduleController,
            financeController,
            wageController
        )

    private inline val selectedController: ActionController get() = controllers[tabPane.selectionModel.selectedIndex]

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        menuBar.isUseSystemMenuBar = SystemUtils.IS_OS_MAC

        drawerList.selectionModel.selectFirst()
        drawerList.selectionModel.selectedItemProperty().listener { _, _, _ ->
            tabPane.selectionModel.select(drawerList.selectionModel.selectedIndex)
            drawer.close()
        }
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED) {
            drawer.open()
            if (!drawerList.isFocused) {
                drawerList.requestFocus()
            }
        }

        later {
            titleLabel.scene.stage.titleProperty().bind(buildStringBinding(
                drawerList.selectionModel.selectedIndexProperty()
            ) {
                "${BuildConfig.NAME} - ${drawerList.selectionModel.selectedItem?.text}"
            })
        }
        titleLabel.textProperty().bind(buildStringBinding(
            tabPane.selectionModel.selectedIndexProperty(),
            *controllers.map { it.titleProperty() }.toTypedArray()
        ) {
            val controller = selectedController
            when {
                controller.titleProperty().hasValue() -> controller.title
                else -> drawerList.selectionModel.selectedItem?.text
            }
        })

        customerGraphic.bind(0, R.image.tab_customer_selected, R.image.tab_customer)
        invoiceGraphic.bind(1, R.image.tab_invoice_selected, R.image.tab_invoice)
        scheduleGraphic.bind(2, R.image.tab_schedule_selected, R.image.tab_schedule)
        financeGraphic.bind(3, R.image.tab_finance_selected, R.image.tab_finance)
        wageGraphic.bind(4, R.image.tab_wage_selected, R.image.tab_wage)

        refresh()

        customerController.replaceButtons()
        tabPane.selectionModel.selectedIndexProperty().listener { _, _, value ->
            controllers[value.toInt()].let {
                it.replaceButtons()
                (it as? Refreshable)?.refresh()
            }
        }

        later {
            employeeLabel.text = login.name
            controllers.forEach {
                it.login = login
                it.rootLayout = rootLayout
            }

            customerController.refresh()
            financeController.addExtra(FinanceController.EXTRA_MAIN_CONTROLLER, this)

            if (login.isFirstTimeLogin) {
                ChangePasswordDialog(this).show { newPassword ->
                    api.editEmployee(login, login.id, newPassword!!, login.isAdmin)
                    rootLayout.jfxSnackbar(getString(R.string.successfully_changed_password), App.DURATION_LONG)
                }
            }
        }
    }

    override fun refresh() {
        eventPagination.contentFactory = Callback { (page, count) ->
            UnselectableListView<Log>().apply {
                styleClass.addAll(R.style.borderless, R.style.list_view_no_scrollbar)
                cellFactory {
                    onUpdate { log, empty ->
                        if (log != null && !empty) graphic = textFlow {
                            text(log.message) {
                                isWrapText = true
                                fontSize = 12.0
                                this@textFlow.prefWidthProperty().bind(this@apply.widthProperty() - 12)
                                wrappingWidthProperty().bind(this@apply.widthProperty())
                            }
                            newLine()
                            text("${log.dateTime.toString(PATTERN_DATETIME)} ") {
                                styleClass += R.style.bold
                            }
                            text(log.login)
                        }
                    }
                }
                GlobalScope.launch(Dispatchers.JavaFx) {
                    val (pageCount, logs) = api.getLogs(page, count)
                    eventPagination.pageCount = pageCount
                    items = logs.toObservableList()
                }
            }
        }
    }

    @FXML fun onDrawerOpening() = refresh()

    @FXML fun onDrawerOpened() = eventPagination.selectLast()

    @FXML fun add(event: ActionEvent) = when (event.source) {
        addCustomerItem -> select(customerController) { later { add() } }
        else -> select(invoiceController) { addInvoice() }
    }

    @FXML fun quit() = App.exit()

    @FXML fun editPrice(event: ActionEvent) = when (event.source) {
        platePriceItem -> EditPlatePriceDialog(this)
        offsetPrintPriceItem -> EditOffsetPriceDialog(this)
        else -> EditDigitalPriceDialog(this)
    }.show()

    @FXML fun editEmployee() = EditEmployeeDialog(this).show()

    @FXML fun editRecess() = EditRecessDialog(this).show()

    @FXML fun settings() = SettingsDialog(this).show()

    @FXML fun testViewInvoice() {
        GlobalScope.launch(Dispatchers.JavaFx) {
            api.getCustomers("", 1, 1).items.firstOrNull()?.let {
                ViewInvoicePopover(
                    this@MainController,
                    Invoice(
                        no = 1234,
                        employeeId = login.id,
                        customerId = it.id,
                        dateTime = Database.dateTime(),
                        offsetJobs = listOf(
                            Invoice.OffsetJob.new(5, "Title", 92000.0, "Type", Technique.TWO_SIDE_EQUAL)
                        ),
                        digitalJobs = listOf(Invoice.DigitalJob.new(5, "Title", 92000.0, "Type", false)),
                        plateJobs = listOf(Invoice.PlateJob.new(5, "Title", 92000.0, "Type")),
                        otherJobs = listOf(Invoice.OtherJob.new(5, "Title", 92000.0)),
                        note = "This is a test",
                        isPrinted = false,
                        isPaid = false,
                        isDone = false
                    ), true
                ).show(menuBar)
            } ?: rootLayout.jfxSnackbar(getString(R.string.no_customer_to_test), App.DURATION_SHORT)
        }
    }

    @FXML fun checkUpdate() = GitHubHelper.checkUpdates(this)

    @FXML fun about() = AboutDialog(this).show()

    private fun ActionController.replaceButtons() = toolbar.setRightItems(*actions.toTypedArray())

    private fun <T : ActionController> select(controller: T, run: T.() -> Unit) {
        drawerList.selectionModel.select(controllers.indexOf(controller))
        controller.run(run)
    }

    private fun MarginedImageView.bind(index: Int, selectedImageId: String, unselectedImageId: String) =
        imageProperty().bind(
            When(drawerList.selectionModel.selectedIndexProperty() eq index)
                then Image(selectedImageId)
                otherwise Image(unselectedImageId)
        )
}