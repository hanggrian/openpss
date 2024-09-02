package com.hanggrian.openpss.ui.main

import com.hanggrian.openpss.BuildConfig
import com.hanggrian.openpss.OpenPssApp
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.MarginedImageView
import com.hanggrian.openpss.control.Toolbar
import com.hanggrian.openpss.db.schemas.Customers
import com.hanggrian.openpss.db.schemas.Employees
import com.hanggrian.openpss.db.schemas.Invoice
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.ui.ActionController
import com.hanggrian.openpss.ui.Controller
import com.hanggrian.openpss.ui.Refreshable
import com.hanggrian.openpss.ui.customer.CustomerController
import com.hanggrian.openpss.ui.employee.EditEmployeeDialog
import com.hanggrian.openpss.ui.finance.FinanceController
import com.hanggrian.openpss.ui.invoice.InvoiceController
import com.hanggrian.openpss.ui.invoice.ViewInvoicePopover
import com.hanggrian.openpss.ui.main.help.AboutDialog
import com.hanggrian.openpss.ui.main.help.GitHubApi
import com.hanggrian.openpss.ui.price.EditDigitalPrintPriceDialog
import com.hanggrian.openpss.ui.price.EditOffsetPrintPriceDialog
import com.hanggrian.openpss.ui.price.EditPlatePriceDialog
import com.hanggrian.openpss.ui.schedule.ScheduleController
import com.hanggrian.openpss.ui.wage.EditRecessDialog
import com.hanggrian.openpss.ui.wage.WageController
import com.jfoenix.controls.JFXDrawer
import com.jfoenix.controls.JFXHamburger
import javafx.beans.binding.When
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.RadioMenuItem
import javafx.scene.control.TabPane
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.bindings.eq
import ktfx.bindings.otherwise
import ktfx.bindings.stringBindingOf
import ktfx.bindings.then
import ktfx.coroutines.listener
import ktfx.jfoenix.controls.jfxSnackbar
import ktfx.jfoenix.controls.show
import ktfx.runLater
import ktfx.windows.stage
import org.apache.commons.lang3.SystemUtils
import org.joda.time.DateTime
import java.net.URL
import java.util.ResourceBundle

class MainController : Controller() {
    @FXML
    override lateinit var stack: StackPane

    @FXML
    lateinit var menuBar: MenuBar

    @FXML
    lateinit var navigateMenu: Menu

    @FXML
    lateinit var addCustomerItem: MenuItem

    @FXML
    lateinit var addInvoiceItem: MenuItem

    @FXML
    lateinit var platePriceItem: MenuItem

    @FXML
    lateinit var offsetPrintPriceItem: MenuItem

    @FXML
    lateinit var digitalPrintPriceItem: MenuItem

    @FXML
    lateinit var drawer: JFXDrawer

    @FXML
    lateinit var drawerList: ListView<Label>

    @FXML
    lateinit var toolbar: Toolbar

    @FXML
    lateinit var hamburger: JFXHamburger

    @FXML
    lateinit var employeeLabel: Label

    @FXML
    lateinit var titleLabel: Label

    @FXML
    lateinit var tabPane: TabPane

    @FXML
    lateinit var customerGraphic: MarginedImageView

    @FXML
    lateinit var invoiceGraphic: MarginedImageView

    @FXML
    lateinit var scheduleGraphic: MarginedImageView

    @FXML
    lateinit var financeGraphic: MarginedImageView

    @FXML
    lateinit var wageGraphic: MarginedImageView

    @FXML
    lateinit var customerController: CustomerController

    @FXML
    lateinit var invoiceController: InvoiceController

    @FXML
    lateinit var scheduleController: ScheduleController

    @FXML
    lateinit var financeController: FinanceController

    @FXML
    lateinit var wageController: WageController

    private val controllers
        get() =
            listOf(
                customerController,
                invoiceController,
                scheduleController,
                financeController,
                wageController,
            )

    private inline val selectedController: ActionController
        get() = controllers[tabPane.selectionModel.selectedIndex]

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        menuBar.isUseSystemMenuBar = SystemUtils.IS_OS_MAC

        navigateMenu.items.forEachIndexed { i, item ->
            item.setOnAction { drawerList.selectionModel.select(i) }
        }

        drawerList.selectionModel.selectedItemProperty().listener { _, _, _ ->
            val index = drawerList.selectionModel.selectedIndex
            tabPane.selectionModel.select(drawerList.selectionModel.selectedIndex)
            if (drawer.isOpened) {
                drawer.close()
            }

            (navigateMenu.items[index] as RadioMenuItem).isSelected = true
        }
        drawerList.selectionModel.selectFirst()
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED) {
            drawer.open()
            if (!drawerList.isFocused) {
                drawerList.requestFocus()
            }
        }

        runLater {
            titleLabel.scene.stage.titleProperty().bind(
                stringBindingOf(drawerList.selectionModel.selectedIndexProperty()) {
                    "${BuildConfig.NAME} - ${drawerList.selectionModel.selectedItem?.text}"
                },
            )
        }
        titleLabel.textProperty().bind(
            stringBindingOf(
                tabPane.selectionModel.selectedIndexProperty(),
                *controllers.map { it.titleProperty }.toTypedArray(),
            ) {
                val controller = selectedController
                when {
                    controller.titleProperty.isNotNull.value -> controller.title
                    else -> drawerList.selectionModel.selectedItem?.text
                }
            },
        )

        customerGraphic.bind(0, R.image_tab_customer_selected, R.image_tab_customer)
        invoiceGraphic.bind(1, R.image_tab_invoice_selected, R.image_tab_invoice)
        scheduleGraphic.bind(2, R.image_tab_schedule_selected, R.image_tab_schedule)
        financeGraphic.bind(3, R.image_tab_finance_selected, R.image_tab_finance)
        wageGraphic.bind(4, R.image_tab_wage_selected, R.image_tab_wage)

        customerController.replaceButtons()
        tabPane.selectionModel.selectedIndexProperty().listener { _, _, value ->
            controllers[value.toInt()].let {
                it.replaceButtons()
                (it as? Refreshable)?.refresh()
            }
        }

        runLater {
            employeeLabel.text = login.name
            controllers.forEach {
                it.login = login
                it.stack = stack
            }

            customerController.refresh()
            financeController.addExtra(FinanceController.EXTRA_MAIN_CONTROLLER, this)

            if (!login.isFirstTimeLogin) {
                return@runLater
            }
            ChangePasswordDialog(this).show { newPassword ->
                transaction {
                    Employees { it.name.equal(login.name) }
                        .projection { password }
                        .update(newPassword!!)
                    stack.jfxSnackbar.show(
                        getString(R.string__password_changed),
                        OpenPssApp.DURATION_LONG,
                    )
                }
            }
        }
    }

    @FXML
    fun add(event: ActionEvent) =
        when (event.source) {
            addCustomerItem -> select(customerController) { runLater { add() } }
            else -> select(invoiceController) { addInvoice() }
        }

    @FXML
    fun quit() = OpenPssApp.exit()

    @FXML
    fun editPrice(event: ActionEvent) =
        when (event.source) {
            platePriceItem -> EditPlatePriceDialog(this)
            offsetPrintPriceItem -> EditOffsetPrintPriceDialog(this)
            else -> EditDigitalPrintPriceDialog(this)
        }.show()

    @FXML
    fun editEmployee() = EditEmployeeDialog(this).show()

    @FXML
    fun editRecess() = EditRecessDialog(this).show()

    @FXML
    fun settings() = SettingsDialog(this).show()

    @FXML
    fun testViewInvoice() {
        transaction { Customers().firstOrNull() }?.let {
            ViewInvoicePopover(
                this,
                Invoice(
                    no = 1234,
                    employeeId = login.id,
                    customerId = it.id,
                    dateTime = DateTime.now(),
                    offsetJobs =
                        listOf(
                            Invoice.OffsetJob.new(
                                5,
                                "Title",
                                92000.0,
                                "Type",
                                Invoice.OffsetJob.Technique.TWO_SIDE_EQUAL,
                            ),
                        ),
                    digitalJobs =
                        listOf(
                            Invoice.DigitalJob.new(
                                5,
                                "Title",
                                92000.0,
                                "Type",
                                false,
                            ),
                        ),
                    plateJobs = listOf(Invoice.PlateJob.new(5, "Title", 92000.0, "Type")),
                    otherJobs = listOf(Invoice.OtherJob.new(5, "Title", 92000.0)),
                    note = "This is a test",
                    isPrinted = false,
                    isPaid = false,
                    isDone = false,
                ),
                true,
            ).show(menuBar)
        } ?: stack.jfxSnackbar
            .show(getString(R.string__no_customer), OpenPssApp.DURATION_SHORT)
    }

    @FXML
    fun checkUpdate() = GitHubApi.checkUpdates(this)

    @FXML
    fun about() = AboutDialog(this).show()

    private fun ActionController.replaceButtons() = toolbar.setRightItems(*actions.toTypedArray())

    private fun <T : ActionController> select(controller: T, run: T.() -> Unit) {
        drawerList.selectionModel.select(controllers.indexOf(controller))
        controller.run(run)
    }

    private fun MarginedImageView.bind(
        index: Int,
        selectedImageId: String,
        unselectedImageId: String,
    ) = imageProperty().bind(
        When(drawerList.selectionModel.selectedIndexProperty() eq index)
            then Image(selectedImageId)
            otherwise Image(unselectedImageId),
    )
}
