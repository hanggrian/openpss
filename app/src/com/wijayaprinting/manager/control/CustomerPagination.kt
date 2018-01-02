package com.wijayaprinting.manager.control

import com.wijayaprinting.data.Customer
import com.wijayaprinting.data.Customers
import com.wijayaprinting.manager.scene.utils.gap
import com.wijayaprinting.manager.utils.safeTransaction
import javafx.beans.property.SimpleStringProperty
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import kotfx.bind
import kotfx.runFX
import kotfx.toObservableList
import org.apache.commons.lang3.math.NumberUtils
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.or
import org.joda.time.DateTime

class CustomerPagination : Pagination() {

    companion object {
        const val COUNT_PER_PAGE = 20
    }

    val textProperty = SimpleStringProperty("").apply { addListener { _, _, _ -> safeTransaction { update() } } }
    var currentPageContainer: StackPane? = null
    val addMenuItem = MenuItem(com.wijayaprinting.manager.R.string.add)
    val deleteMenuItem = MenuItem(com.wijayaprinting.manager.R.string.delete)

    init {
        if (!com.wijayaprinting.manager.BuildConfig.DEBUG) {
            setPageFactory { page -> getPageContainer(page) }
            contextMenu = ContextMenu(addMenuItem, deleteMenuItem)
            addMenuItem.setOnAction {
                CustomerDialog().showAndWait()
                        .filter { it is Array<*> }
                        .ifPresent { (mName, mEmail, mPhone1, mPhone2, mNote) ->
                            safeTransaction {
                                Customer.new {
                                    name = mName as String
                                    since = DateTime.now()
                                    //email = mEmail as? String
                                    //phone1 = mPhone1 as String?
                                    //phone2 = mPhone2 as String?
                                    //note = mNote as String?
                                }
                                update()
                            }
                        }
            }
            safeTransaction { update() }
        }
    }

    val customers: SizedIterable<Customer>
        get() = if (textProperty.value.isEmpty()) Customer.all()
        else Customer.find {
            var condition = Customers.name regexp textProperty.value or
                    (Customers.email regexp textProperty.value) or
                    (Customers.phone1 regexp textProperty.value) or
                    (Customers.phone2 regexp textProperty.value) or
                    (Customers.note regexp textProperty.value)
            if (NumberUtils.isDigits(textProperty.value)) {
                condition = condition or (Customers.id eq textProperty.value.toInt())
            }
            condition
        }

    fun update() {
        pageCount = (customers.count() / COUNT_PER_PAGE) + 1
        if (currentPageContainer != null) {
            currentPageContainer!!.fireEvent(PageEvent(currentPageIndex))
        }
    }

    private fun getPageContainer(page: Int): Node {
        currentPageContainer = StackPane().apply {
            children.add(getPageListView(page))
        }
        currentPageContainer!!.addEventHandler(PageEvent.UPDATE, PageHandler())
        return currentPageContainer!!
    }

    private fun getPageListView(page: Int): Node {
        val listView = ListView<Customer>()
        safeTransaction {
            listView.items = customers.limit(COUNT_PER_PAGE, COUNT_PER_PAGE * page).toList().toObservableList()
        }
        return listView
    }

    class PageEvent(page: Int) : Event(UPDATE) {
        companion object {
            val UPDATE: EventType<PageEvent> = EventType(Event.ANY, "UPDATE")
        }

        var page = 1

        init {
            this.page = page
        }
    }

    inner class PageHandler : EventHandler<PageEvent> {
        override fun handle(event: PageEvent) {
            currentPageContainer!!.children.setAll(getPageListView(event.page))
        }
    }

    class CustomerDialog(customer: Customer? = null) : Dialog<Array<*>>() {
        val nameField = TextField().apply { promptText = "Name" }
        val emailField = TextField().apply { promptText = "Email" }
        val phone1Field = TextField().apply { promptText = "Phone 1" }
        val phone2Field = TextField().apply { promptText = "Phone 2" }
        val noteArea = TextArea().apply { promptText = "Note" }

        init {
            title = customer?.toString() ?: "Add customer"
            graphic = ImageView(com.wijayaprinting.manager.R.png.ic_user)
            headerText = customer?.toString() ?: "Add customer"
            dialogPane.content = GridPane().apply {
                gap(8.0)
                add(Label("Name"), 0, 0)
                add(nameField, 1, 0)
                add(Label("Email"), 0, 1)
                add(emailField, 1, 1)
                add(Label("Phone 1"), 0, 2)
                add(phone1Field, 1, 2)
                add(Label("Phone 2"), 0, 3)
                add(phone2Field, 1, 3)
                add(Label("Note"), 0, 4)
                add(noteArea, 1, 4)
            }
            dialogPane.buttonTypes.addAll(ButtonType.CANCEL, ButtonType.OK)
            dialogPane.lookupButton(ButtonType.OK).disableProperty() bind nameField.textProperty().isEmpty
            runFX { nameField.requestFocus() }
            setResultConverter {
                if (it == ButtonType.OK) arrayOf(
                        nameField.text,
                        emailField.textOrNull,
                        phone1Field.textOrNull,
                        phone2Field.textOrNull,
                        noteArea.textOrNull
                ) else null
            }
        }

        val TextInputControl.textOrNull: String? get() = if (text.isEmpty()) null else text
    }
}