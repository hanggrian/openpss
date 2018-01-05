package com.wijayaprinting.manager.control

import com.wijayaprinting.dao.Customer
import com.wijayaprinting.dao.Customers
import com.wijayaprinting.manager.BuildConfig.DEBUG
import com.wijayaprinting.manager.utils.safeTransaction
import javafx.beans.property.SimpleStringProperty
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Node
import javafx.scene.control.ListView
import javafx.scene.control.Pagination
import javafx.scene.layout.StackPane
import kotfx.toObservableList
import org.apache.commons.lang3.math.NumberUtils
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.or

class CustomerPagination : Pagination() {

    companion object {
        const val COUNT_PER_PAGE = 20
    }

    val textProperty = SimpleStringProperty("").apply { addListener { _, _, _ -> safeTransaction { update() } } }
    var currentPageContainer: StackPane? = null

    init {
        if (!DEBUG) {
            setPageFactory { page -> getPageContainer(page) }
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
        safeTransaction { listView.items = customers.limit(COUNT_PER_PAGE, COUNT_PER_PAGE * page).toObservableList() }
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
}